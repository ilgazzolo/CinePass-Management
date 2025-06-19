package com.api.boleteria.validators;

import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.BadRequestException;

public class CinemaValidator {

    public static void validateFields(CinemaRequestDTO dto) {
        // Validación del nombre
        if (dto.getNombre() == null || dto.getNombre().isBlank()) {
            throw new BadRequestException("El nombre no puede ser nulo ni estar vacío.");
        }
        if (dto.getNombre().isEmpty() || dto.getNombre().length() > 100) {
            throw new BadRequestException("El nombre debe tener entre 1 y 100 caracteres.");
        }

        // Tipo de pantalla
        if (dto.getScreenType() == null) {
            throw new BadRequestException("El tipo de pantalla no puede ser nulo.");
        }

        // Capacidad
        if (dto.getCapacity() == null) {
            throw new BadRequestException("La capacidad no puede ser nula.");
        }
        if (dto.getCapacity() < 1 || dto.getCapacity() > 200) {
            throw new BadRequestException("La capacidad debe ser entre 1 y 500.");
        }

        // Atmos
        if (dto.getAtmos() == null) {
            throw new BadRequestException("El atributo Atmos no puede ser nulo.");
        }

        // Habilitada
        if (dto.getHabilitada() == null) {
            throw new BadRequestException("El atributo 'habilitada' no puede ser nulo.");
        }
    }

}
