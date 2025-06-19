package com.api.boleteria.service;

import com.api.boleteria.dto.detail.TicketDetailDTO;
import com.api.boleteria.dto.request.TicketRequestDTO;
import com.api.boleteria.exception.AccesDeniedException;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Card;
import com.api.boleteria.model.Ticket;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.ICardRepository;
import com.api.boleteria.repository.ITicketRepository;
import com.api.boleteria.repository.IFunctionRepository;
import com.api.boleteria.repository.IUserRepository;
import com.api.boleteria.validators.BoletoValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar operaciones relacionadas con tickets.
 */
@Service
@RequiredArgsConstructor
public class TicketService {

    private final ITicketRepository ticketRepository;
    private final ICardRepository cardRepository;
    private final IUserRepository userRepository;
    private final IFunctionRepository functionRepository;
    private final UserService userService;

    public static final double TICKET_PRICE = 2500.0;

    /**
     * Crea uno o varios tickets para una función específica.
     * Verifica la disponibilidad de entradas y el saldo en la tarjeta del usuario.
     *
     * @param dto DTO con los datos de la compra (ID de función y cantidad).
     * @return Lista de TicketDetailDTO con los tickets comprados.
     */
    public List<TicketDetailDTO> buyTickets(TicketRequestDTO dto) {
        BoletoValidator.validateFields(dto);
        User user = userService.getUsernameAuthenticatedUser();

        // verifico si existe la funcion
        Function function = functionRepository.findById(dto.getFunctionId())
                .orElseThrow(() -> new NotFoundException("Función no encontrada."));

        int requestedQuantity = dto.getQuantity();
        BoletoValidator.validateCapacity(function, requestedQuantity);

        // verifico que exista la tarjeta
        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario " + user.getUsername() + " no tiene una tarjeta registrada."));

        BoletoValidator.validateCardBalance(card, requestedQuantity);

        double totalAmount = TICKET_PRICE * requestedQuantity;

        // Descuento saldo y capacidad
        card.setBalance(card.getBalance() - totalAmount);
        function.setAvailableCapacity(function.getAvailableCapacity() - requestedQuantity);
        cardRepository.save(card);
        functionRepository.save(function);

        List<Ticket> createdTickets = new ArrayList<>();

        for (int i = 0; i < requestedQuantity; i++) {
            Ticket ticket = new Ticket();
            ticket.setTicketPrice(TICKET_PRICE);
            ticket.setPurchaseDateTime(LocalDateTime.now());
            ticket.setUser(user);
            ticket.setFunction(function);

            Ticket saved = ticketRepository.save(ticket);
            createdTickets.add(saved);
            user.getTickets().add(saved);
        }

        userRepository.save(user);

        return createdTickets.stream()
                .map(ticket -> new TicketDetailDTO(
                        ticket.getId(),
                        ticket.getPurchaseDateTime().toLocalDate().toString(),
                        function.getMovie().getTitle(),
                        function.getCinema().getId(),
                        ticket.getPurchaseDateTime().toLocalTime().toString(),
                        ticket.getTicketPrice()
                ))
                .toList();
    }


    /**
     * Obtiene todos los tickets asociados al usuario autenticado.
     *
     * @return Lista de BoletoDetailDTO con los tickets del usuario.
     */
    public List<TicketDetailDTO> findTicketsFromAuthenticatedUser() {
        User user = userService.getUsernameAuthenticatedUser();

        return user.getTickets().stream().map(ticket -> {
            String movieTitle = ticket.getFunction().getMovie().getTitle();
            Long roomId = ticket.getFunction().getCinema().getId();
            String purchaseTime = ticket.getPurchaseDateTime().toLocalTime().toString();

            return new TicketDetailDTO(
                    ticket.getId(),
                    ticket.getPurchaseDateTime().toLocalDate().toString(),
                    movieTitle,
                    roomId,
                    purchaseTime,
                    ticket.getTicketPrice()
            );
        }).toList();
    }


    /**
     * Obtiene un ticket específico por su ID, validando que pertenezca al usuario autenticado.
     *
     * @param ticketId ID del ticket a buscar.
     * @return BoletoDetailDTO con los datos del ticket.
     * @throws AccesDeniedException si el ticket no pertenece al usuario autenticado.
     */
    public TicketDetailDTO findTicketById(Long ticketId) {
        User user = userService.getUsernameAuthenticatedUser();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("No se encontró el ticket con ID: " + ticketId));

        // verifica que el ticket sea del usuario activo
        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new AccesDeniedException("No tiene permiso para ver este ticket.");
        }

        String movieTitle = ticket.getFunction().getMovie().getTitle();
        Long roomId = ticket.getFunction().getCinema().getId();
        String purchaseTime = ticket.getPurchaseDateTime().toLocalTime().toString();

        return new TicketDetailDTO(
                ticket.getId(),
                ticket.getPurchaseDateTime().toLocalDate().toString(),
                movieTitle,
                roomId,
                purchaseTime,
                ticket.getTicketPrice()
        );
    }
}
