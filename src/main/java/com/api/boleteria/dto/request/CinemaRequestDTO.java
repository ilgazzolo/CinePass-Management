package com.api.boleteria.dto.request;

import com.api.boleteria.model.TipoPantalla;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotNull(message = "El tipo de pantalla es obligatorio")
    private TipoPantalla tipoPantalla;

    @NotNull(message = "El campo atmos es obligatorio")
    private Boolean atmos;

    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad mínima es 1")
    private Integer capacity;

    @NotNull(message = "El campo habilitada es obligatorio")
    private Boolean habilitada;

}
