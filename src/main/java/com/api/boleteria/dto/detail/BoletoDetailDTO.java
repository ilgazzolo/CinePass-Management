package com.api.boleteria.dto.detail;

public record BoletoDetailDTO(
        Long id,
        Double precio,
        String fechaCompra,
        Long usuarioId,
        String usuarioNombre,
        Long funcionId
) {}
