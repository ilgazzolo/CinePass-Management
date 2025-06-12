package com.api.boleteria.dto.detail;



import java.util.List;

public record MovieDetailDTO(
        Long id,
        String title,
        Integer min,
        String genre,
        String director,
        String rating,
        String synopsis,
        List<Long> functionListID
){}
