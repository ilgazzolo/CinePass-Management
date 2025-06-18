package com.api.boleteria.validators;

import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.BadRequestException;

public class CinemaValidator {

    public static void validateCinema(CinemaRequestDTO dto){
        if (dto.getNombre() == null) {
            throw new BadRequestException("El nombre no puede ser nulo.");
        }
        if (dto.getScreenType() == null) {
            throw new BadRequestException("El tipo de pantalla no puede ser nulo.");
        }
        if (dto.getCapacity() <= 0) {
            throw new BadRequestException("No es una capacidad valida.");
        }
        if(dto.getAtmos() == null){
            throw new BadRequestException("El atributo Atmos no puede ser nulo");
        }
        if (dto.getHabilitada() == null){
            throw new BadRequestException("No puede tener un atributo Habilitada nulo.");
        }
    }
}
