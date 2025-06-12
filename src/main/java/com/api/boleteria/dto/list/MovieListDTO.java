package com.api.boleteria.dto.list;

public record MovieListDTO(
        Long id,
        String title,
        Integer min,
        String genre,
        String director
) {}
