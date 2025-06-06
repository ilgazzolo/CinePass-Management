package com.api.boleteria.dto.list;

public record UserListDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        String role
) {
}
