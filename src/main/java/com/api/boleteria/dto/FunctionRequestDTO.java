package com.api.boleteria.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class FunctionRequestDTO {
    @NotNull(message = "this field is required")
    @Past(message = "the date cannot be passed")
    private LocalDateTime date;

    @NotNull(message = "this field is required")
    private Long cinemaId;

    @NotNull(message = "this field is required")
    private Long movieId;
}
