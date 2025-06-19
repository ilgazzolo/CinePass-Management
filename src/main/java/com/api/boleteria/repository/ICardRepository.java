package com.api.boleteria.repository;

import com.api.boleteria.model.Card;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ICardRepository extends JpaRepository<Card, Long> {
    Optional<Card> findByUserId(Long userId);
}
