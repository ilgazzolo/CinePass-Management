package com.api.boleteria.validators;
import com.api.boleteria.dto.request.UserRequestDTO;


public class UserValidator {

    public static void CamposValidator(UserRequestDTO dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new IllegalArgumentException("El nombre no puede estar vacío.");
        }
        if (dto.getSurname() == null || dto.getSurname().isBlank()) {
            throw new IllegalArgumentException("El apellido no puede estar vacío.");
        }
        if (dto.getUsername() == null || dto.getUsername().isBlank()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío.");
        }
        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new IllegalArgumentException("La contraseña no puede estar vacía.");
        }
        if (dto.getEmail() == null || dto.getEmail().isBlank() || !dto.getEmail().contains("@")) {
            throw new IllegalArgumentException("El email no es válido.");
        }
        if (dto.getRole() == null) {
            throw new IllegalArgumentException("El rol es obligatorio.");
        }



    }
}