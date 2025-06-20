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
import com.api.boleteria.validators.TicketValidator;
import lombok.RequiredArgsConstructor;
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
     * Convierte una entidad Ticket a un DTO detallado.
     * @param ticket entidad de ticket a convertir
     * @return TicketDetailDTO con los datos relevantes del ticket
     */
    private TicketDetailDTO mapToDetailDTO(Ticket ticket) {
        return new TicketDetailDTO(
                ticket.getId(),
                ticket.getPurchaseDateTime().toLocalDate().toString(),
                ticket.getFunction().getMovie().getTitle(),
                ticket.getFunction().getCinema().getId(),
                ticket.getPurchaseDateTime().toLocalTime().toString(),
                ticket.getTicketPrice()
        );
    }

    /**
     * Crea uno o varios tickets para una función específica.
     * Verifica la disponibilidad de entradas y el saldo en la tarjeta del usuario.
     *
     * @param dto DTO con los datos de la compra (ID de función y cantidad).
     * @return Lista de TicketDetailDTO con los tickets comprados.
     */
    public List<TicketDetailDTO> buyTickets(TicketRequestDTO dto) {
        TicketValidator.validateFields(dto);
        User user = userService.getUsernameAuthenticatedUser();

        Function function = functionRepository.findById(dto.getFunctionId())
                .orElseThrow(() -> new NotFoundException("Función no encontrada."));

        int requestedQuantity = dto.getQuantity();
        TicketValidator.validateCapacity(function, requestedQuantity);

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario " + user.getUsername() + " no tiene una tarjeta registrada."));

        TicketValidator.validateCardBalance(card, requestedQuantity);

        double totalAmount = TICKET_PRICE * requestedQuantity;

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
                .map(this::mapToDetailDTO)
                .toList();
    }

    /**
     * Obtiene todos los tickets asociados al usuario autenticado.
     *
     * @return Lista de TicketDetailDTO con los tickets del usuario.
     */
    public List<TicketDetailDTO> findTicketsFromAuthenticatedUser() {
        User user = userService.getUsernameAuthenticatedUser();

        return user.getTickets().stream()
                .map(this::mapToDetailDTO)
                .toList();
    }

    /**
     * Obtiene un ticket específico por su ID, validando que pertenezca al usuario autenticado.
     *
     * @param ticketId ID del ticket a buscar.
     * @return TicketDetailDTO con los datos del ticket.
     * @throws AccesDeniedException si el ticket no pertenece al usuario autenticado.
     */
    public TicketDetailDTO findTicketById(Long ticketId) {
        User user = userService.getUsernameAuthenticatedUser();

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("No se encontró el ticket con ID: " + ticketId));

        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new AccesDeniedException("No tiene permiso para ver este ticket.");
        }

        return mapToDetailDTO(ticket);
    }
}
