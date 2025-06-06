package com.api.boleteria.dto.detail.UsuarioDetail;

import com.api.boleteria.dto.detail.UserDetailDTO;

public record ClienteDetailDTO(
        Long id,
        UserDetailDTO usuarioDetailDTO
) {
}
