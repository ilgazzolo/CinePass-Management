package com.api.boleteria.repository;

import com.api.boleteria.model.Function;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IFunctionRepository extends JpaRepository<Function, Long> {
}
