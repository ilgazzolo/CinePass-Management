package com.api.boleteria.dto.request;

import com.api.boleteria.model.Function;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CinemaRequestDTO {
    @NotNull(message = "this field is required")
    private Integer capacity;


    private List<Function> functions;
}
