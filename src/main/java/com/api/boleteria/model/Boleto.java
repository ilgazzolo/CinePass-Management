package com.api.boleteria.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "boletos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Boleto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double precio;

    @Column(nullable = false)
    private LocalDateTime fechaCompra;

    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "funcion_id", nullable = false)
    private Function funcion;
}
