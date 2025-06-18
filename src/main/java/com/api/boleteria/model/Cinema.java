package com.api.boleteria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cinemas")
public class Cinema {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // es el número de sala

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoPantalla tipoPantalla;

    @Column(nullable = false)
    private Boolean atmos;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false)
    private Boolean habilitada;

    @OneToMany(mappedBy = "cinema", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Function> functionList = new ArrayList<>();
}
