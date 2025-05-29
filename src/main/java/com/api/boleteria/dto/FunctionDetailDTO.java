package com.api.boleteria.dto;

import java.time.LocalDateTime;

public record FunctionDetailDTO(
        Long id,
        String date,
        Long cinemaId,
        Long movieId,
        String movieName
) {}
