package com.api.boleteria.service;

import com.api.boleteria.dto.detail.BoletoDetailDTO;
import com.api.boleteria.dto.request.BoletoRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Boleto;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.IBoletoRepository;
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
public class BoletoService {

    private final IBoletoRepository boletoRepo;
    private final IUserRepository usuarioRepo;
    private final IFunctionRepository functionRepo;
    private final IUserRepository userRepo;

    private static final double PRECIO_BOLETO = 2500.0; // Precio fijo para todos

    public BoletoDetailDTO create(BoletoRequestDTO dto) {
        BoletoValidator.validarCampos(dto);

        // Obtener el nombre de usuario desde el contexto de seguridad
        String username = SecurityContextHolder.getContext().getAuthentication().getName();

        // Buscar al usuario por username
        User user = usuarioRepo.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario no encontrado"));

        Function funcion = functionRepo.findById(dto.funcionId())
                .orElseThrow(() -> new NotFoundException("Función no encontrada"));

        if (funcion.getCapacidadDisponible() <= 0) {
            throw new BadRequestException("No hay más entradas disponibles.");
        }

        funcion.setCapacidadDisponible(funcion.getCapacidadDisponible() - 1);
        functionRepo.save(funcion);

        Boleto boleto = new Boleto();
        boleto.setPrecio(PRECIO_BOLETO);
        boleto.setFechaCompra(LocalDateTime.now());
        boleto.setUser(user);
        boleto.setFuncion(funcion);

        Boleto saved = boletoRepo.save(boleto);

        // Agregar boleto al usuario (opcional si tenés cascade)
        user.getBoletos().add(saved);
        usuarioRepo.save(user);

        return new BoletoDetailDTO(
                saved.getId(),
                saved.getFechaCompra().toLocalDate().toString(),  // Ej: 2025-06-17
                funcion.getMovie().getTitle(),                    // Título de la película
                funcion.getCinema().getId(),                      // ID de la sala
                saved.getFechaCompra().toLocalTime().toString(),  // Ej: 19:45:00
                saved.getPrecio()
        );
    }



    public List<BoletoDetailDTO> getBoletosDelUsuarioLogueado() {
        // 1) Obtener username del usuario autenticado
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();

        // 2) Buscar el usuario
        User usuario = userRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        // 3) Mapear a DTOs
        return usuario.getBoletos().stream().map(boleto -> {
            String tituloPelicula = boleto.getFuncion().getMovie().getTitle();
            Long idSala = boleto.getFuncion().getCinema().getId();
            String horaCompra = boleto.getFechaCompra().toLocalTime().toString();

            return new BoletoDetailDTO(
                    boleto.getId(),
                    boleto.getFechaCompra().toLocalDate().toString(),
                    tituloPelicula,
                    idSala,
                    horaCompra,
                    boleto.getPrecio()
            );
        }).toList();
    }






}
