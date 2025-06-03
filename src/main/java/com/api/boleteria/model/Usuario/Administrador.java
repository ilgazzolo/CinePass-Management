package com.api.boleteria.model.Usuario;

import jakarta.persistence.*;

@Entity
@Table(name = "administradores")
public class Administrador {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "usuario_id",nullable = false)
    private Usuario usuario;


}
