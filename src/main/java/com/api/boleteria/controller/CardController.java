package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.CardDetailDTO;
import com.api.boleteria.dto.request.CardRequestDTO;
import com.api.boleteria.service.CardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping("/api/client/card")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public ResponseEntity<CardDetailDTO> getCard() {
        return ResponseEntity.ok(cardService.findFromAuthenticatedUser());
    }


    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping
    public ResponseEntity<CardDetailDTO> createCard(@RequestBody @Valid CardRequestDTO dto) {
        return ResponseEntity.ok(cardService.save(dto));
    }


    @PreAuthorize("hasRole('CLIENT')")
    @PatchMapping("/recharge")
    public ResponseEntity<CardDetailDTO> recharge(@RequestParam Double amount) {
        return ResponseEntity.ok(cardService.rechargeBalance(amount));
    }


    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/balance")
    public ResponseEntity<Double> getBalance() {
        return ResponseEntity.ok(cardService.getBalance());
    }


    @PreAuthorize("hasRole('CLIENT')")
    @PutMapping
    public ResponseEntity<CardDetailDTO> updateCard(@RequestBody @Valid CardRequestDTO dto) {
        return ResponseEntity.ok(cardService.updateAuthenticatedUserCard(dto));
    }


    @PreAuthorize("hasRole('CLIENT')")
    @DeleteMapping
    public ResponseEntity<Void> deleteCard() {
        cardService.deleteFromAuthenticatedUser();
        return ResponseEntity.noContent().build();
    }


}

