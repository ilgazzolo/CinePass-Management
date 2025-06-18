package com.api.boleteria.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class MovieRequestDTO {

    @NotBlank(message = "El titulo es obligatorio")
    private String title;

    @NotNull(message = "La duracion es obligatoria")
    private Integer duration;

    @NotBlank(message = "El genero es obligatorio")
    private String genre;

    @NotBlank(message = "El director es obligatorio")
    private String director;

    @NotBlank(message = "El genero es obligatorio")
    private String rating;

    @NotBlank(message = "La sinopsis es obligatoria")
    private String synopsis;

    private List<Long> functionListId;
}
