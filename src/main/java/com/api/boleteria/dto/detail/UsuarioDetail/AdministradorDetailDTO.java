package com.api.boleteria.dto.detail.UsuarioDetail;

import com.api.boleteria.dto.detail.UserDetailDTO;

public record AdministradorDetailDTO(
        Long id,
        UserDetailDTO usuarioDetailDTO
) {
}
