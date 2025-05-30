package com.api.boleteria.repository;

import com.api.boleteria.model.Cinema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ICinemaRepository extends JpaRepository<Cinema, Long> {
}
