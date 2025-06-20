package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.MovieDetailDTO;
import com.api.boleteria.dto.list.MovieListDTO;
import com.api.boleteria.dto.request.MovieRequestDTO;
import com.api.boleteria.service.MovieService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de películas.
 *
 * Permite crear, obtener, actualizar y eliminar películas, así como
 * buscar películas por género.
 *
 * Las operaciones de creación, actualización y eliminación están restringidas
 * a usuarios con rol ADMIN, mientras que la consulta está permitida también
 * para usuarios con rol CLIENT.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/movies")
public class MovieController {

    @Autowired
    private MovieService movieService;

    /**
     * Registra una o más películas.
     *
     * Este endpoint permite a un administrador cargar múltiples películas en una sola solicitud.
     * Cada película es validada y registrada si no existe previamente una con el mismo título.
     *
     * @param entity Lista de DTOs con la información necesaria para crear las películas.
     * @return ResponseEntity con la lista de películas creadas.
     */
    @PostMapping("/register")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<MovieDetailDTO>> create(@Valid @RequestBody List<@Valid MovieRequestDTO> entity) {
        return ResponseEntity.status(HttpStatus.CREATED).body(movieService.createAll(entity));
    }

    /**
     * Obtiene la lista de todas las películas.
     *
     * @return ResponseEntity con una lista de películas o lanza RuntimeException si la lista está vacía.
     */

    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<MovieListDTO>> getAll(){
        List<MovieListDTO> movieList = movieService.findAll();
        if (movieList.isEmpty()){
            throw new RuntimeException("Null");
        }
        return ResponseEntity.ok(movieList);
    }

    /**
     * Obtiene el detalle de una película específica por su ID.
     *
     * @param id Identificador de la película.
     * @return ResponseEntity con el detalle de la película.
     */

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<MovieDetailDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(movieService.findById(id));
    }

    /**
     * Elimina una película por su ID.
     *
     * @param id Identificador de la película a eliminar.
     * @return ResponseEntity con estado 204 No Content si la eliminación fue exitosa.
     */

    @DeleteMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        movieService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza una película por su ID.
     *
     * @param id Identificador de la película a actualizar.
     * @param entity DTO con la nueva información para la película.
     * @return ResponseEntity con el detalle actualizado de la película.
     */

    @PutMapping("/update/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MovieDetailDTO> update(@PathVariable Long id, @Valid @RequestBody MovieRequestDTO entity){
        return ResponseEntity.ok(movieService.updateById(id, entity));
    }

    /**
     * Obtiene la lista de películas filtradas por género.
     *
     * @param genre Género para filtrar las películas.
     * @return ResponseEntity con la lista de películas que coinciden con el género.
     */

    @GetMapping("/genre/{genre}")
    public ResponseEntity<List<MovieListDTO>> findByGenre(@PathVariable String genre) {
        return ResponseEntity.ok(movieService.findByMovieGenre(genre));
    }
}
