package com.api.boleteria.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "movies")
public class Movie {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private Integer duration;

    @Column(nullable = false)
    private String movieGenre;

    @Column(nullable = false)
    private String director;

    @Column(nullable = false)
    private String ageRating;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String synopsis;

    @OneToMany
    private List<Function> functions = new ArrayList<>();

}
