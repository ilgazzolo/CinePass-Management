package com.api.boleteria.repository;

import com.api.boleteria.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IMovieRepository extends JpaRepository<Movie,Long> {

    boolean existsByTitle(String title);
}
