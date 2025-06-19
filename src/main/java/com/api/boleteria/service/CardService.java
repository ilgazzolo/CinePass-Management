package com.api.boleteria.service;

import com.api.boleteria.dto.detail.CardDetailDTO;
import com.api.boleteria.dto.request.CardRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Card;
import com.api.boleteria.model.User;
import com.api.boleteria.repository.ICardRepository;
import com.api.boleteria.repository.IUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class CardService {

    private final ICardRepository cardRepository;
    private final IUserRepository userRepository;

    private static final double MAX_RECHARGE_AMOUNT = 20000.0;
    private static final double MAX_TOTAL_BALANCE = 1000000.0;


    public CardDetailDTO save (CardRequestDTO dto) {

        Card card = new Card();
        card.setCardNumber(dto.getCardNumber());
        card.setCardholderName(dto.getCardholderName());
        card.setExpirationDate(dto.getExpirationDate());
        card.setCvv(dto.getCvv());
        card.setCardType(dto.getCardType());
        card.setBalance(0.0);
        card.setUser(getAuthenticatedUser());

        Card saved = cardRepository.save(card);

        return mapToDetailDTO(saved);
    }



    public CardDetailDTO rechargeBalance(Double amount) {
        if (amount == null || amount <= 0) {
            throw new BadRequestException("El monto debe ser mayor que cero.");
        }

        if (amount > MAX_RECHARGE_AMOUNT) {
            throw new BadRequestException("El monto excede el límite máximo de recarga permitido: $" + MAX_RECHARGE_AMOUNT);
        }


        User user = getAuthenticatedUser();

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario: "+user.getUsername()+" no tiene una tarjeta registrada."));

        if (card.getBalance() + amount > MAX_TOTAL_BALANCE) {
            throw new BadRequestException("El saldo total no puede superar $" + MAX_TOTAL_BALANCE);
        }

        card.setBalance(card.getBalance() + amount);
        cardRepository.save(card);

        return mapToDetailDTO(card);
    }


    public Double getBalance() {
        User user = getAuthenticatedUser();
        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("El usuario: "+user.getUsername()+" no tiene una tarjeta registrada."));
        return card.getBalance();
    }


    public CardDetailDTO findFromAuthenticatedUser() {
        User user = getAuthenticatedUser();

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("No se encontró tarjeta para el usuario: "+user.getUsername()));

        return mapToDetailDTO(card);
    }



    public CardDetailDTO updateAuthenticatedUserCard(CardRequestDTO dto) {
        User user = getAuthenticatedUser();

        return cardRepository.findByUserId(user.getId())
                .map(card -> {
                    card.setCardNumber(dto.getCardNumber());
                    card.setCardholderName(dto.getCardholderName());
                    card.setExpirationDate(dto.getExpirationDate());
                    card.setIssueDate(dto.getIssueDate());
                    card.setCvv(dto.getCvv());
                    card.setCardType(dto.getCardType());

                    Card updated = cardRepository.save(card);

                    return mapToDetailDTO(card);
                })
                .orElseThrow(() -> new NotFoundException("No se encontró tarjeta para el usuario: " + user.getUsername()));
    }



    public void deleteFromAuthenticatedUser() {
        User user = getAuthenticatedUser();

        Card card = cardRepository.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("No se encontró tarjeta para el usuario: "+user.getUsername()));

        cardRepository.delete(card);
    }



    private String getAuthenticatedUsername() {
        return SecurityContextHolder.getContext().getAuthentication().getName();
    }



    private User getAuthenticatedUser() {
        String username = getAuthenticatedUsername();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Usuario: "+username+" no encontrado. "));
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
