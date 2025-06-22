package com.api.boleteria.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter @Setter
public class MovieRequestListDTO {

    @NotEmpty(message = "La lista de películas no puede estar vacía")
    @Valid                           // 🔑 activa las validaciones de cada MovieRequestDTO
    private List<MovieRequestDTO> movies;
}
