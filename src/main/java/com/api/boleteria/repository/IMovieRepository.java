package com.api.boleteria.repository;

import com.api.boleteria.model.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IMovieRepository extends JpaRepository<Movie,Long> {

    boolean existsByTitle(String title);
}
