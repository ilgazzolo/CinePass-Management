package com.api.boleteria.validators;

import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.BadRequestException;

/**
 * Clase encargada de validar los datos de entrada para la entidad Cinema.
 *
 * Realiza validaciones sobre los campos obligatorios y rangos permitidos
 * para los atributos de un CinemaRequestDTO.
 */
public class CinemaValidator {

    /**
     * Valida todos los campos del DTO de solicitud de Cinema.
     *
     * @param dto DTO con los datos del cine a validar.
     * @throws BadRequestException si alguna validación falla.
     */
    public static void validateFields(CinemaRequestDTO dto) {
        validateNombre(dto.getNombre());
        validateScreenType(dto.getScreenType());
        validateCapacity(dto.getCapacity());
        validateAtmos(dto.getAtmos());
        validateHabilitada(dto.getHabilitada());
    }

    /**
     * Valida el nombre del cine.
     *
     * @param nombre Nombre a validar.
     * @throws BadRequestException si el nombre es nulo, vacío o excede 100 caracteres.
     */
    private static void validateNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            throw new BadRequestException("El nombre no puede ser nulo ni estar vacío.");
        }
        if (nombre.length() > 100) {
            throw new BadRequestException("El nombre debe tener máximo 100 caracteres.");
        }
    }

    /**
     * Valida el tipo de pantalla.
     *
     * @param screenType Tipo de pantalla a validar.
     * @throws BadRequestException si es nulo.
     */
    private static void validateScreenType(Object screenType) {
        if (screenType == null) {
            throw new BadRequestException("El tipo de pantalla no puede ser nulo.");
        }
    }

    /**
     * Valida la capacidad de la sala.
     *
     * @param capacity Capacidad a validar.
     * @throws BadRequestException si es nula o no está entre 1 y 200.
     */
    private static void validateCapacity(Integer capacity) {
        if (capacity == null) {
            throw new BadRequestException("La capacidad no puede ser nula.");
        }
        if (capacity < 1 || capacity > 200) {
            throw new BadRequestException("La capacidad debe ser entre 1 y 200.");
        }
    }

    /**
     * Valida el atributo Atmos.
     *
     * @param atmos Valor a validar.
     * @throws BadRequestException si es nulo.
     */
    private static void validateAtmos(Boolean atmos) {
        if (atmos == null) {
            throw new BadRequestException("El atributo Atmos no puede ser nulo.");
        }
    }

    /**
     * Valida si la sala está habilitada.
     *
     * @param habilitada Valor a validar.
     * @throws BadRequestException si es nulo.
     */
    private static void validateHabilitada(Boolean habilitada) {
        if (habilitada == null) {
            throw new BadRequestException("El atributo 'habilitada' no puede ser nulo.");
        }
    }
}
