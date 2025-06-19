package com.api.boleteria.validators;

import com.api.boleteria.dto.request.TicketRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.model.Card;
import com.api.boleteria.model.Function;

import static com.api.boleteria.service.TicketService.TICKET_PRICE;

public class TicketValidator {

    public static void validateFields (TicketRequestDTO dto) {
        if (dto.getFunctionId() == null || dto.getFunctionId() <= 0) {
            throw new BadRequestException("El ID de la función es inválido.");
        }

        if (dto.getQuantity() == null || dto.getQuantity() <= 0) {
            throw new BadRequestException("La cantidad de tickets debe ser mayor a cero.");
        }
    }


    public static void validateCapacity(Function function, int requestedQuantity) {
        if (function.getAvailableCapacity() < requestedQuantity) {
            throw new BadRequestException("No hay suficientes entradas disponibles. Solo quedan: " + function.getAvailableCapacity() + ".");
        }
    }


    public static void validateCardBalance(Card card, int requestedQuantity) {
        double total = TICKET_PRICE * requestedQuantity;
        if (card.getBalance() < total) {
            throw new BadRequestException("Fondos insuficientes en la tarjeta. Total requerido: $" + total);
        }
    }

}


