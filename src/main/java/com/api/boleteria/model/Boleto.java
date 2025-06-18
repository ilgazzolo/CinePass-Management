package com.api.boleteria.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
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



    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    @JsonBackReference
    private User user;


    @ManyToOne
    @JoinColumn(name = "funcion_id", nullable = false)
    private Function funcion;
}
