package com.api.boleteria.controller;


import com.api.boleteria.dto.detail.MovieDetailDTO;
import com.api.boleteria.dto.list.MovieListDTO;
import com.api.boleteria.dto.request.MovieRequestDTO;
import com.api.boleteria.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/peliculas")
public class MovieController {

    @Autowired
    private MovieService movieService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDetailDTO> create (@Valid @RequestBody MovieRequestDTO req){
        return ResponseEntity.ok(movieService.create(req));
    }


    @GetMapping
    public ResponseEntity<List<MovieListDTO>> getAll(){
        List<MovieListDTO> movieList = movieService.findAll();
        if (movieList.isEmpty()){
            throw new RuntimeException("Null");
        }
        return ResponseEntity.ok(movieList);
    }


    @GetMapping("/{id}")
    public ResponseEntity<MovieDetailDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(movieService.findById(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDetailDTO> update(@PathVariable Long id, @Valid @RequestBody MovieRequestDTO req){
        return ResponseEntity.ok(movieService.updateById(id, req));
    }


}
