package com.api.boleteria.repository;

import com.api.boleteria.model.Boleto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface IBoletoRepository extends JpaRepository<Boleto, Long> {
    List<Boleto> findByUserId(Long userId);
}
