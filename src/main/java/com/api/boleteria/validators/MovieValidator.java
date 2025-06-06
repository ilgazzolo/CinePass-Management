package com.api.boleteria.validators;

import com.api.boleteria.dto.request.MovieRequestDTO;
import org.springframework.stereotype.Component;

@Component
public class MovieValidator {


        public static void CamposValidator(MovieRequestDTO dto) {
            if (dto.getTitle() == null || dto.getTitle().isBlank()) {
                throw new IllegalArgumentException("El título no puede estar vacío.");
            }
            if (dto.getDuration() == null || dto.getDuration() > 0) {
                throw new IllegalArgumentException("La duración debe ser mayor que 0.");
            }
            if (dto.getGenre() == null || dto.getGenre().isBlank()) {
                throw new IllegalArgumentException("El género no puede estar vacío.");
            }
            if (dto.getDirector() == null || dto.getDirector().isBlank()) {
                throw new IllegalArgumentException("El director no puede estar vacío.");
            }
            if (dto.getRating() == null || !dto.getRating().isBlank()) {
                throw new IllegalArgumentException("El rating no es válido.");
            }
            if (dto.getSynopsis() == null || dto.getSynopsis().isBlank()) {
                throw new IllegalArgumentException("La sinopsis no puede estar vacía.");
            }
            if (dto.getFunctionId() == null || dto.getFunctionId() <= 0) {
                throw new IllegalArgumentException("El ID de función es obligatorio y debe ser positivo.");
            }
        }



}
