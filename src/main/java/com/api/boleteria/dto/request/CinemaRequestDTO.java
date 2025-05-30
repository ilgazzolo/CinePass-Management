package com.api.boleteria.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CinemaRequestDTO {
    @NotNull(message = "this field is required")
    private Integer capacity;


}
