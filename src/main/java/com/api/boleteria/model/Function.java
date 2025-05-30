package com.api.boleteria.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Past;
import lombok.*;
import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "funciones")
public class Function {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime date;

    @ManyToOne
    @Column(nullable = false)
    private Cinema cinema;

    //@ManyToOne
    //@Column(nullable = false)
    //private Movie movie;
}
