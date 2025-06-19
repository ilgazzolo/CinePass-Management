package com.api.boleteria.service;

import com.api.boleteria.dto.detail.MovieDetailDTO;
import com.api.boleteria.dto.list.MovieListDTO;
import com.api.boleteria.dto.request.MovieRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.Movie;
import com.api.boleteria.repository.IFunctionRepository;
import com.api.boleteria.repository.IMovieRepository;
import com.api.boleteria.validators.MovieValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * Servicio para gestionar operaciones relacionadas con Peliculas.
 */
@Service
@RequiredArgsConstructor
public class MovieService {

    private final IMovieRepository movieRepository;

    private final IFunctionRepository functionRepository;

    /**
     * crea una nueva pelicula
     * @param req MovieRequets con lainformacion del nuevo usuario
     * @return MovieDetail con la informacion del usuario creado
     */
    public MovieDetailDTO create(MovieRequestDTO req) {
        // Validación de campos del DTO
        MovieValidator.CamposValidator(req);

        // Validación de existencia por título
        if (movieRepository.existsByTitle(req.getTitle().trim())) {
            throw new BadRequestException("Ya existe una película con el título: " + req.getTitle());
        }

        Movie movie = new Movie();
        movie.setTitle(req.getTitle().trim());
        movie.setDuration(req.getDuration());
        movie.setMovieGenre(req.getGenre());
        movie.setDirector(req.getDirector());
        movie.setClassification(req.getRating());
        movie.setSynopsis(req.getSynopsis());
        //movie.setFunctionList(functionList);

        Movie saved = movieRepository.save(movie);

        return new MovieDetailDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getDuration(),
                saved.getMovieGenre(),
                saved.getDirector(),
                saved.getClassification(),
                saved.getSynopsis(),
                saved.getFunctions().stream()
                        .map(Function::getId)
                        .toList()
                );

    }


    public boolean existsByTittle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        return movieRepository.existsByTitle(title.trim());
    }


    /**
     * mustra todas las peliculas asociadas a un genero en especifico
     * @param genre genero de la pelicula a mostrar
     * @return lista de MovieList con la informacion de las peliculas encontradas
     */
    public List<MovieListDTO> findByMovieGenre(String genre) {
        return movieRepository.findByMovieGenre(genre).stream()
                .map(movie -> new MovieListDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDuration(),
                        movie.getMovieGenre(),
                        movie.getDirector()
                ))
                .toList();
    }


    /**
     * obtiene todas las peliculas cargadas
     * @return lista de MovieList con la informacion de las peliculas encontradas
     */
    public List<MovieListDTO> findAll(){
        return movieRepository.findAll().stream().
                map(movie -> new MovieListDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDuration(),
                        movie.getMovieGenre(),
                        movie.getDirector()
                ))
                .toList();
    }


    /**
     * obtiene una pelicula segun un ID especificado
     * @param id ID de la pelcula a buscar
     * @return MovieDetail con la informacion de la pelicula encontrada
     */
    public MovieDetailDTO findById(Long id){
        Movie m = movieRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("La pelicula con ID: "+id+" no fue encontrada."));

        return new MovieDetailDTO(
                m.getId(),
                m.getTitle(),
                m.getDuration(),
                m.getMovieGenre(),
                m.getDirector(),
                m.getClassification(),
                m.getSynopsis(),
                m.getFunctions().stream()
                        .map(Function::getId)
                        .toList()
        );
    }


    /**
     * actualiza una pelicula, segun un ID especificado
     * @param id ID de la pelicula a actualizar
     * @param req DTO con los cambios realizados
     * @return MovieDetail con la informacion de la pelicula actualizada
     */
    public MovieDetailDTO updateById(Long id, MovieRequestDTO req){
        return movieRepository.findById(id).
                map(movie -> {
                    movie.setTitle(req.getTitle());
                    movie.setDuration(req.getDuration());
                    movie.setMovieGenre(req.getGenre());
                    movie.setDirector(req.getDirector());
                    movie.setClassification(req.getRating());
                    movie.setSynopsis(req.getSynopsis());

                    List<Function> functions = functionRepository.findAllById(req.getFunctionListId());

                    movie.setFunctions(functions);

                    Movie update = movieRepository.save(movie);

                    return new MovieDetailDTO(
                            update.getId(),
                            update.getTitle(),
                            update.getDuration(),
                            update.getMovieGenre(),
                            update.getDirector(),
                            update.getClassification(),
                            update.getSynopsis(),
                            update.getFunctions().stream()
                                    .map(Function::getId)
                                    .toList()
                    );
                })
                .orElseThrow(()-> new NotFoundException("La pelicula con ID: "+id+" no fue encontrada."));
    }


    /**
     * elimina una pelicula segun un ID especficado
     * @param id ID de la pelicula a eliminar
     */
    public void deleteById(Long id){
        if(!movieRepository.existsById(id)){
            throw new NotFoundException("La pelicula con ID: "+id+" no fue encontrada.");
        }
        movieRepository.deleteById(id);
    }





}






