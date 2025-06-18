package com.api.boleteria.dto.request;

import jakarta.validation.constraints.Positive;

public record BoletoRequestDTO(
        @Positive
        Long funcionId
) {}
