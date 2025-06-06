package com.api.boleteria.dto.list.UsuarioList;

import com.api.boleteria.dto.list.UserListDTO;

public record ClienteListDTO(
        Long id,
        UserListDTO usuarioListDTO
) {
}
