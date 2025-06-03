package com.api.boleteria.repository.UsuarioRepositories;

import com.api.boleteria.model.Usuario.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
}
