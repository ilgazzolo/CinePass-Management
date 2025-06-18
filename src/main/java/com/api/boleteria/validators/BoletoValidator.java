package com.api.boleteria.validators;

import com.api.boleteria.dto.request.BoletoRequestDTO;
import com.api.boleteria.exception.BadRequestException;

public class BoletoValidator {

    public static void validarCampos(BoletoRequestDTO dto) {
        if (dto.funcionId() == null || dto.funcionId() <= 0) {
            throw new BadRequestException("El ID de la función es inválido.");
        }
    }
}
