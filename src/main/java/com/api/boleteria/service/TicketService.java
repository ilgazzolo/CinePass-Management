package com.api.boleteria.service;

import com.api.boleteria.dto.detail.BoletoDetailDTO;
import com.api.boleteria.dto.request.BoletoRequestDTO;
import com.api.boleteria.exception.AccesDeniedException;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Ticket;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.User;
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

@Service
@RequiredArgsConstructor
public class TicketService {

    private final ITicketRepository boletoRepo;

    private final IUserRepository usuarioRepo;

    private final IFunctionRepository functionRepo;

    private final IUserRepository userRepo;

    private static final double PRECIO_BOLETO = 2500.0;

    
    public BoletoDetailDTO create(BoletoRequestDTO dto) {
        BoletoValidator.validarCampos(dto);

        // Obtener el nombre de usuario desde el contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar al usuario por username
        User user = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Function funcion = functionRepo.findById(dto.funcionId())
                .orElseThrow(() -> new NotFoundException("Función no encontrada"));

        if (funcion.getAvailableCapacity() <= 0) {
            throw new BadRequestException("No hay más entradas disponibles.");
        }

        funcion.setAvailableCapacity(funcion.getAvailableCapacity() - 1);
        functionRepo.save(funcion);

        Ticket ticket = new Ticket();
        ticket.setTicketPrice(PRECIO_BOLETO);
        ticket.setPurchaseDateTime(LocalDateTime.now());
        ticket.setUser(user);
        ticket.setFunction(funcion);

        Ticket saved = boletoRepo.save(ticket);

        // Agregar boleto al usuario (opcional si tenés cascade)
        user.getTickets().add(saved);
        usuarioRepo.save(user);

        return new BoletoDetailDTO(
                saved.getId(),
                saved.getPurchaseDateTime().toLocalDate().toString(),  // Ej: 2025-06-17
                funcion.getMovie().getTitle(),                    // Título de la película
                funcion.getCinema().getRoomId(),                      // ID de la sala
                saved.getPurchaseDateTime().toLocalTime().toString(),  // Ej: 19:45:00
                saved.getTicketPrice()
        );
    }



    public List<BoletoDetailDTO> getBoletosDelUsuarioLogueado() {
        //  Obtener username del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        //  Buscar el usuario
        User usuario = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // Mapear a DTOs
        return usuario.getTickets().stream().map(boleto -> {
            String tituloPelicula = boleto.getFunction().getMovie().getTitle();
            Long idSala = boleto.getFunction().getCinema().getRoomId();
            String horaCompra = boleto.getPurchaseDateTime().toLocalTime().toString();

            return new BoletoDetailDTO(
                    boleto.getId(),
                    boleto.getPurchaseDateTime().toLocalDate().toString(),
                    tituloPelicula,
                    idSala,
                    horaCompra,
                    boleto.getTicketPrice()
            );
        }).toList();
    }

    public BoletoDetailDTO getBoletoById(Long idBoleto){

        //  Obtener username del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        //  Buscar el usuario
        User usuario = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        //  Buscar el boleto
        Ticket ticket = boletoRepo.findById(idBoleto)
                .orElseThrow(() -> new NotFoundException("No se encontró el boleto con ID: " + idBoleto));

        //  Verificar que el boleto pertenezca al usuario autenticado
        if (!ticket.getUser().getId().equals(usuario.getId())) {
            throw new AccesDeniedException("No tiene permiso para ver este boleto.");
        }

        //  Obtener datos necesarios
        String tituloPelicula = ticket.getFunction().getMovie().getTitle();
        Long idSala = ticket.getFunction().getCinema().getRoomId();
        String horaCompra = ticket.getPurchaseDateTime().toLocalTime().toString();

        // Devolver DTO
        return new BoletoDetailDTO(
                ticket.getId(),
                ticket.getPurchaseDateTime().toLocalDate().toString(),
                tituloPelicula,
                idSala,
                horaCompra,
                ticket.getTicketPrice()
        );
    }




}
