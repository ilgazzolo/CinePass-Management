package com.api.boleteria.dto.detail;

public record BoletoDetailDTO(
        Long id,
        String fechaCompra,
        String tituloPelicula,
        Long idSala,
        String horaCompra,
        Double precio

) {}
