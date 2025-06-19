package com.api.boleteria.validators;

import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.Movie;

import java.time.LocalDateTime;
import java.util.List;

public class FunctionValidator {

    public static void validateFields(FunctionRequestDTO dto) {
        if (dto.getShowtime() == null) {
            throw new BadRequestException("La date de la función no puede ser nula.");
        }

        if (dto.getShowtime().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La date de la función no puede estar en el pasado.");
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
        LocalDateTime nuevaInicio = dto.getShowtime();
        LocalDateTime nuevaFin = nuevaInicio.plusMinutes(movie.getDuration());

        for (Function f : funcionesEnSala) {
            LocalDateTime existenteInicio = f.getShowtime();
            LocalDateTime existenteFin = existenteInicio.plusMinutes(f.getMovie().getDuration());

            boolean seSolapan = nuevaInicio.isBefore(existenteFin) && existenteInicio.isBefore(nuevaFin);
            if (seSolapan) {
                throw new BadRequestException("Ya existe una función en esa sala que se solapa con el horario.");
            }
        }
    }

    // valida que no se creen funciones para un maximo de dos años
    public static void validateMaxTwoYears(FunctionRequestDTO dto) {
        if (dto.getShowtime().isAfter(LocalDateTime.now().plusYears(2))) {
            throw new BadRequestException("La fecha de la función es demasiado lejana.");
        }
    }

    public static void validateEnabledCinema(Cinema cinema) {
        if (!cinema.getEnabled()) {
            throw new BadRequestException("La sala " + cinema.getName() + " no está habilitada.");
        }
    }



}
