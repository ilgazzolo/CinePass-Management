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

    private static final double PRECIO_TICKET = 2500.0;

    /**
     * Crea un nuevo ticket para la función especificada.
     * Verifica la capacidad disponible y descuenta 1 al generar el ticket.
     *
     * @param dto DTO con la información de la compra de ticket.
     * @return BoletoDetailDTO con los datos del ticket creado.
     */
    public TicketDetailDTO create(TicketRequestDTO dto) {
        BoletoValidator.validarCampos(dto);

        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario con nombre: "+username+" no encontrado"));

        Function function = functionRepository.findById(dto.funcionId())
                .orElseThrow(() -> new NotFoundException("Función no encontrada"));

        if (function.getAvailableCapacity() <= 0) {
            throw new BadRequestException("No hay más entradas disponibles.");
        }

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario: "+username+" no tiene una tarjeta registrada."));

        if (card.getBalance() < PRECIO_TICKET) {
            throw new BadRequestException("Fondos insuficientes en la tarjeta.");
        }

        card.setBalance(card.getBalance() - PRECIO_TICKET);
        cardRepository.save(card);

        function.setAvailableCapacity(function.getAvailableCapacity() - 1);
        functionRepository.save(function);


        Ticket ticket = new Ticket();
        ticket.setTicketPrice(PRECIO_TICKET);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setUser(user);
        ticket.setFunction(function);

        Ticket savedTicket = ticketRepository.save(ticket);

        user.getTickets().add(savedTicket);
        userRepository.save(user);

        return new TicketDetailDTO(
                savedTicket.getId(),
                savedTicket.getPurchaseDateTime().toLocalDate().toString(),
                function.getMovie().getTitle(),
                function.getCinema().getRoomId(),
                savedTicket.getPurchaseDateTime().toLocalTime().toString(),
                savedTicket.getTicketPrice()
        );
    }


    /**
     * Obtiene todos los tickets asociados al usuario autenticado.
     *
     * @return Lista de BoletoDetailDTO con los tickets del usuario.
     */
    public List<TicketDetailDTO> findTicketsFromAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con nombre: "+username+" no encontrado"));

        return user.getTickets().stream().map(ticket -> {
            String movieTitle = ticket.getFunction().getMovie().getTitle();
            Long roomId = ticket.getFunction().getCinema().getRoomId();
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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario con nombre: "+username+" no encontrado"));

        Ticket ticket = ticketRepository.findById(ticketId)
                .orElseThrow(() -> new NotFoundException("No se encontró el ticket con ID: " + ticketId));

        if (!ticket.getUser().getId().equals(user.getId())) {
            throw new AccesDeniedException("No tiene permiso para ver este ticket.");
        }

        String movieTitle = ticket.getFunction().getMovie().getTitle();
        Long roomId = ticket.getFunction().getCinema().getRoomId();
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
