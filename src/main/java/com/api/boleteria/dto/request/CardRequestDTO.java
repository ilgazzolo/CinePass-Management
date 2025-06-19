package com.api.boleteria.dto.request;

import com.api.boleteria.model.CardType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
public class CardRequestDTO {

    @NotBlank(message = "El numero de tarjeta es obligatorio")
    @Pattern(regexp = "^[0-9]{16}$", message = "El número de tarjeta debe tener exactamente 16 dígitos")
    private String cardNumber;

    @NotBlank(message = "El nombre del titular es obligatorio")
    @Size(min = 3)
    private String cardholderName;

    @NotNull(message = "La fecha de vencimiento es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Debe ingresar la fecha de vencimiento en un formato como: 'MM/YY'")
    private String expirationDate;

    @NotNull(message = "La fecha de emisión es obligatoria")
    @Pattern(regexp = "^(0[1-9]|1[0-2])/\\d{2}$", message = "Debe ingresar la fecha de emisión en un formato como: 'MM/YY'")
    private String  issueDate;

    @NotBlank(message = "El codigo de seguridad es obligatorio")
    @Size(max = 4)
    @JsonIgnore
    private String cvv;

    @NotNull(message = "El tipo de tarjeta es obligatorio.")
    private CardType cardType;


    @NotNull(message = "Debe tener un usuario asosiado. ")
    private Long userId;

}
