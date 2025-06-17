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
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class BoletoService {

    private final IBoletoRepository boletoRepo;
    private final IUserRepository usuarioRepo;
    private final IFunctionRepository functionRepo;

    private static final double PRECIO_BOLETO = 2500.0; // Precio fijo para todos

    public BoletoDetailDTO create(BoletoRequestDTO dto) {
        BoletoValidator.validarCampos(dto);

        User user = usuarioRepo.findById(dto.usuarioId())
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

        ///  agrego boleto al usuario
        user.getBoletos().add(saved);
        usuarioRepo.save(user);

        return new BoletoDetailDTO(
                saved.getId(),
                saved.getPrecio(),
                saved.getFechaCompra().toString(),
                user.getId(),
                user.getName(),
                funcion.getId()
        );
    }
}
