package com.api.boleteria.dto.list;

public record MovieListDTO(
        Long id,
        String title,
        String duration,
        String genre,
        String director,
        String rating,
        String synopsis,
        Long functionId
) {}
