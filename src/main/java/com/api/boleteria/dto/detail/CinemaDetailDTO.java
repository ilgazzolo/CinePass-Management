package com.api.boleteria.dto.detail;

import com.api.boleteria.model.Function;
import com.api.boleteria.model.TipoPantalla;

import java.util.List;

public record CinemaDetailDTO (
        Long id,
        String nombre,
        TipoPantalla tipoPantalla,
        Boolean atmos,
        Integer capacity,
        Boolean habilitada
){}
