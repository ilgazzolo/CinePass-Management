package com.api.boleteria.dto.request.UsuarioRequest;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UsuarioRequestDTO {
    @NotBlank(message = "El nombre no puede ser nulo")
    @Size(min = 1,max = 50, message = "Min 1 caracter, Max 50 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido no puede ser nulo")
    @Size(min = 1,max = 50, message = "Min 1 caracter, Max 50 caracteres")
    private String apellido;

    @NotBlank(message = "El email no puede ser nulo")
    @Email
    private String email;

    @NotBlank(message = "La contrasenia no puede ser nula")
    @Size(min=5, message = "Min 5 caracteres")
    private String contrasenia;


}
