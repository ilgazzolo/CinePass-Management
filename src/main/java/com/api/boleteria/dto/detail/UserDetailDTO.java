package com.api.boleteria.dto.detail;

public record UserDetailDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        String password,
        String role
) {}

