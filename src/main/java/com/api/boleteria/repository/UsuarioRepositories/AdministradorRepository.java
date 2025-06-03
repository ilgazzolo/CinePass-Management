package com.api.boleteria.repository.UsuarioRepositories;

import com.api.boleteria.model.Usuario.Administrador;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdministradorRepository extends JpaRepository<Administrador,Long> {
}
