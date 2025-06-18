package com.api.boleteria.dto.detail;

import com.api.boleteria.model.ScreenType;

public record CinemaDetailDTO (
        Long id,
        String name,
        ScreenType screenType,
        Boolean atmos,
        Integer seatCapacity,
        Boolean enabled
){}
