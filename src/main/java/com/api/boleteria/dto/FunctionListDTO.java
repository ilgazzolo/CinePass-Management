package com.api.boleteria.dto;

import java.time.LocalDate;
import java.time.LocalTime;

public record FunctionListDTO(
        Long id,
        LocalDate date,
        LocalTime time,
        Long cinema,
        String movieName
) {}
