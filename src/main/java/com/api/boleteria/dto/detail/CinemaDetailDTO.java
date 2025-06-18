package com.api.boleteria.dto.detail;

import com.api.boleteria.model.ScreenType;

public record CinemaDetailDTO (
        Long id,
        String nombre,
        ScreenType screenType,
        Boolean atmos,
        Integer capacity,
        Boolean habilitada
){}
