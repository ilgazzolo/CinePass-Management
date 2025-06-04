package com.api.boleteria.validators;

import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.Movie;

import java.time.LocalDateTime;
import java.util.List;

public class FunctionValidator {

    public static void validate(FunctionRequestDTO dto) {
        if (dto.getDate() == null) {
            throw new BadRequestException("La fecha de la función no puede ser nula.");
        }

        if (dto.getDate().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la función no puede estar en el pasado.");
        }

        if (dto.getCinemaId() == null || dto.getCinemaId() <= 0) {
            throw new BadRequestException("El ID del cine no es válido.");
        }

        if (dto.getMovieId() == null || dto.getMovieId() <= 0) {
            throw new BadRequestException("El ID de la película no es válido.");
        }
    }

    // Validación de solapamiento de horarios en la misma sala
    public static void validateHorario(FunctionRequestDTO dto, Movie movie, List<Function> funcionesEnSala) {
        LocalDateTime nuevaInicio = dto.getDate();
        LocalDateTime nuevaFin = nuevaInicio.plusMinutes(movie.getDuration());

        for (Function f : funcionesEnSala) {
            LocalDateTime existenteInicio = f.getDate();
            LocalDateTime existenteFin = existenteInicio.plusMinutes(f.getMovie().getDuration());

            boolean seSolapan = nuevaInicio.isBefore(existenteFin) && existenteInicio.isBefore(nuevaFin);
            if (seSolapan) {
                throw new BadRequestException("Ya existe una función en esa sala que se solapa con el horario.");
            }
        }
    }


}
