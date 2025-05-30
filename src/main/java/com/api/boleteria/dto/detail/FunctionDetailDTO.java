package com.api.boleteria.dto.detail;

public record FunctionDetailDTO(
        Long id,
        String date,
        Long cinemaId,
        Long movieId,
        String movieName
) {}
