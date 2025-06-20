package com.api.boleteria.validators;

import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.Movie;

import java.time.LocalDateTime;
import java.util.List;

public class FunctionValidator {

    /**
     * Valida todos los campos del DTO FunctionRequestDTO.
     *
     * @param dto DTO con los datos de la función a validar.
     * @throws BadRequestException si algún campo no cumple las reglas de validación.
     */
    public static void validateFields(FunctionRequestDTO dto) {
        validateShowtime(dto.getShowtime());
        validateCinemaId(dto.getCinemaId());
        validateMovieId(dto.getMovieId());
    }

    /**
     * Valida la fecha y hora de la función.
     *
     * @param showtime Fecha y hora de la función.
     * @throws BadRequestException si la fecha es nula o está en el pasado.
     */
    private static void validateShowtime(LocalDateTime showtime) {
        if (showtime == null) {
            throw new BadRequestException("La fecha de la función no puede ser nula.");
        }
        if (showtime.isBefore(LocalDateTime.now())) {
            throw new BadRequestException("La fecha de la función no puede estar en el pasado.");
        }
    }

    /**
     * Valida el ID del cine.
     *
     * @param cinemaId ID del cine.
     * @throws BadRequestException si el ID es nulo o menor o igual a cero.
     */
    private static void validateCinemaId(Long cinemaId) {
        if (cinemaId == null || cinemaId <= 0) {
            throw new BadRequestException("El ID del cine no es válido.");
        }
    }

    /**
     * Valida el ID de la película.
     *
     * @param movieId ID de la película.
     * @throws BadRequestException si el ID es nulo o menor o igual a cero.
     */
    private static void validateMovieId(Long movieId) {
        if (movieId == null || movieId <= 0) {
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
