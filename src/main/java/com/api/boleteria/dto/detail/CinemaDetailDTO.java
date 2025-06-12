package com.api.boleteria.dto.detail;

import com.api.boleteria.model.Function;

import java.util.List;

public record CinemaDetailDTO (
        Long id,
        Integer capacity,
        List<Function> functions // creo que con DTO está bien
) {}
