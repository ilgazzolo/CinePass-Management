package com.api.boleteria.dto.list;

import com.api.boleteria.model.Function;

import java.util.List;

public record MovieListDTO(
        Long id,
        String title,
        Integer duration,
        String genre,
        String director,
        String rating,
        String synopsis,
        List<Function> functionList
) {}
