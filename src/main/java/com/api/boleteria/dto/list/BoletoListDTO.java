package com.api.boleteria.dto.list;

import java.time.LocalDateTime;

public record BoletoListDTO(
        Long id,
        Long funcionId,
        String tituloPelicula,
        LocalDateTime fecha,
        Double precio
) {}
