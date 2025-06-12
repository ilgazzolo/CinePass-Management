package com.api.boleteria.dto.list;

import com.api.boleteria.model.Function;

import java.util.List;

public record CinemaListDTO (
    Long cinemaId,
    Integer capacity
){}
