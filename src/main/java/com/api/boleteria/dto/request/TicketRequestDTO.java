package com.api.boleteria.dto.request;

import jakarta.validation.constraints.Positive;

public record TicketRequestDTO(
        @Positive
        Long funcionId
) {}
