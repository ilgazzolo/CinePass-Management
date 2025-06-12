package com.api.boleteria.dto.request;

import com.api.boleteria.model.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserRequestDTO {
    @NotBlank(message = "El nombre no puede ser nulo")
    @Size(min = 1,max = 50, message = "Min 1 caracter, Max 50 caracteres")
    private String name;

    @NotBlank(message = "El apellido no puede ser nulo")
    @Size(min = 1,max = 50, message = "Min 1 caracter, Max 50 caracteres")
    private String surname;

    @Email(message = "El email debe tener un formato valido")
    private String email;

    @NotBlank(message = "La contrasenia no puede ser nula")
    @Size(min=5, message = "Min 5 caracteres")
    private String password;

    @NotNull(message = "El rol es obligatorio")
    private Role role;
}
