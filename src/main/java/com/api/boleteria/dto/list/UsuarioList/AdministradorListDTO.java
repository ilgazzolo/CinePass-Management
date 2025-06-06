package com.api.boleteria.dto.list.UsuarioList;

import com.api.boleteria.dto.list.UserListDTO;

public record AdministradorListDTO(
        Long id,
        UserListDTO usuarioListDTO
) {
}
