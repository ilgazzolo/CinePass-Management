package com.api.boleteria.service;

import com.api.boleteria.dto.detail.CardDetailDTO;
import com.api.boleteria.dto.request.CardRequestDTO;
import com.api.boleteria.exception.BadRequestException; //
import com.api.boleteria.exception.NotFoundException; //
import com.api.boleteria.model.Card; //
import com.api.boleteria.model.User; //
import com.api.boleteria.repository.ICardRepository; //
import com.api.boleteria.repository.IUserRepository;
import com.api.boleteria.validators.CardValidator; //
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

/**
 * Servicio para la gestión de tarjetas de los usuarios.
 *
 * Proporciona operaciones para crear, actualizar, recargar saldo,
 * obtener detalles y eliminar la tarjeta asociada al usuario autenticado.
 */
@Service
@RequiredArgsConstructor
public class CardService {

    private final ICardRepository cardRepository;
    private final IUserRepository userRepository;
    private final CardValidator cardValidator;
    private final UserService userService;

    private static final double MAX_RECHARGE_AMOUNT = 20000.0;
    private static final double MAX_TOTAL_BALANCE = 1000000.0;


    //-------------------------------SAVE--------------------------------//

    /**
     * Crea una nueva tarjeta para el usuario autenticado.
     *
     * @param dto Datos necesarios para crear la tarjeta.
     * @return DTO con el detalle de la tarjeta creada.
     * @throws BadRequestException si el número de tarjeta ya está en uso.
     */
    public CardDetailDTO save(CardRequestDTO dto) {
        cardValidator.validateCard(dto);

        // Validar si el número de tarjeta ya existe globalmente
        if (cardRepository.existsByCardNumber(dto.getCardNumber())) { //
            throw new BadRequestException("El número de tarjeta '" + dto.getCardNumber() + "' ya está registrado."); //
        }

        Card card = mapToEntity(dto);
        Card saved = cardRepository.save(card);
        return mapToDetailDTO(saved);
    }



    //-------------------------------GET/FIND--------------------------------//

    /**
     * Obtiene el saldo actual de la tarjeta del usuario autenticado.
     *
     * @return Saldo actual.
     */
    public Double getBalance() {
        User user = userService.findAuthenticatedUser();
        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario: " + user.getUsername() + " no tiene una tarjeta registrada."));
        return card.getBalance();
    }

    /**
     * Obtiene el detalle de la tarjeta del usuario autenticado.
     *
     * @return DTO con el detalle de la tarjeta.
     */
    public CardDetailDTO findFromAuthenticatedUser() {
        User user = userService.findAuthenticatedUser();

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("No se encontró tarjeta para el usuario: " + user.getUsername()));

        return mapToDetailDTO(card);
    }



    //-------------------------------UPDATE--------------------------------//

    /**
     * Recarga saldo a la tarjeta del usuario autenticado.
     *
     * @param amount Monto a recargar.
     * @return DTO con el detalle actualizado.
     */
    public CardDetailDTO rechargeBalance(Double amount) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("El monto debe ser mayor que cero.");
        }

        if (amount > MAX_RECHARGE_AMOUNT) {
            throw new BadRequestException("El monto excede el límite máximo de recarga permitido: $" + MAX_RECHARGE_AMOUNT);
        }

        User user = userService.findAuthenticatedUser();

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario: " + user.getUsername() + " no tiene una tarjeta registrada."));

        if (card.getBalance() + amount > MAX_TOTAL_BALANCE) {
            throw new BadRequestException("El saldo total no puede superar $" + MAX_TOTAL_BALANCE);
        }

        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);

        return mapToDetailDTO(card);
    }

    /**
     * Actualiza los datos de la tarjeta del usuario autenticado.
     *
     * @param dto DTO con los datos nuevos.
     * @return DTO actualizado.
     * @throws BadRequestException si el número de tarjeta ya está en uso por otra tarjeta.
     */

    public CardDetailDTO updateAuthenticatedUserCard(CardRequestDTO dto) {
        cardValidator.validateCard(dto);
        User user = userService.findAuthenticatedUser();

        return cardRepository.findByUserId(user.getId())
                .map(card -> {
                    // Validar si el nuevo número de tarjeta ya existe para otra tarjeta (excluyendo la tarjeta actual)
                    if (!card.getCardNumber().equals(dto.getCardNumber()) && cardRepository.existsByCardNumberAndIdNot(dto.getCardNumber(), card.getId())) { //
                        throw new BadRequestException("El número de tarjeta '" + dto.getCardNumber() + "' ya está registrado por otra tarjeta."); //
                    }

                    card.setCardNumber(dto.getCardNumber());
                    card.setCardholderName(dto.getCardholderName());
                    card.setExpirationDate(dto.getExpirationDate());
                    card.setIssueDate(dto.getIssueDate());
                    card.setCvv(dto.getCvv());
                    card.setCardType(dto.getCardType());

                    Card updated = cardRepository.save(card);
                    return mapToDetailDTO(updated);
                })
                .orElseThrow(() -> new NotFoundException("No se encontró tarjeta para el usuario: " + user.getUsername()));
    }



    //-------------------------------DELETE--------------------------------//

    /**
     * Elimina la tarjeta asociada al usuario autenticado.
     */
    public void deleteFromAuthenticatedUser() {
        User user = userService.findAuthenticatedUser();

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("No se encontró tarjeta para el usuario: " + user.getUsername()));

        cardRepository.delete(card);
    }


    //-------------------------------MAPS--------------------------------//

    private Card mapToEntity(CardRequestDTO dto) {
        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setCardholderName(dto.getCardholderName());
        card.setExpirationDate(dto.getExpirationDate());
        card.setIssueDate(dto.getIssueDate());
        card.setCvv(dto.getCvv());
        card.setCardType(dto.getCardType());
        card.setBalance(0.0);
        card.setUser(userService.findAuthenticatedUser());
        return card;
    }

    private CardDetailDTO mapToDetailDTO(Card card) {
        return new CardDetailDTO(
                card.getId(),
                card.getCardNumber(),
                card.getCardholderName(),
                card.getExpirationDate(),
                card.getIssueDate(),
                card.getCardType().toString().toUpperCase(),
                card.getBalance(),
                card.getUser().getId()
        );
    }
}