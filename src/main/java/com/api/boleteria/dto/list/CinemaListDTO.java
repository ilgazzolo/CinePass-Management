package com.api.boleteria.dto.list;

import com.api.boleteria.model.Function;
import com.api.boleteria.model.TipoPantalla;

import java.util.List;

public record CinemaListDTO (
    Long id,
    String nombre,
    Integer capacity,
    Boolean habilitada
){}
