package com.api.boleteria.dto.list;

public record CinemaListDTO (
    Long id,
    String nombre,
    Integer capacity,
    Boolean habilitada
){}
