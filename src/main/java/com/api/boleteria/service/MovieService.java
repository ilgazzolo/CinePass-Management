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

@Service
@RequiredArgsConstructor
public class MovieService {

    private final IMovieRepository movieRepository;

    private final IFunctionRepository functionRepository;

    /**
     * crea una nueva pelicula
     * @param req MovieRequets del nuevo usuario
     * @return MovieDetail
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
        movie.setAgeRating(req.getRating());
        movie.setSynopsis(req.getSynopsis());
        //movie.setFunctionList(functionList);

        Movie saved = movieRepository.save(movie);

        // Devolver DTO
        return new MovieDetailDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getDuration(),
                saved.getMovieGenre(),
                saved.getDirector(),
                saved.getAgeRating(),
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


    public MovieDetailDTO findById(Long id){
        Movie m = movieRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("La pelicula con ID: "+id+" no fue encontrada."));

        return new MovieDetailDTO(
                m.getId(),
                m.getTitle(),
                m.getDuration(),
                m.getMovieGenre(),
                m.getDirector(),
                m.getAgeRating(),
                m.getSynopsis(),
                m.getFunctions().stream()
                        .map(Function::getId)
                        .toList()
        );
    }


    public MovieDetailDTO updateById(Long id, MovieRequestDTO entity){
        return movieRepository.findById(id).
                map(movie -> {
                    movie.setTitle(entity.getTitle());
                    movie.setDuration(entity.getDuration());
                    movie.setMovieGenre(entity.getGenre());
                    movie.setDirector(entity.getDirector());
                    movie.setAgeRating(entity.getRating());
                    movie.setSynopsis(entity.getSynopsis());

                    List<Function> functions = functionRepository.findAllById(entity.getFunctionListId());

                    movie.setFunctions(functions);

                    Movie update = movieRepository.save(movie);

                    return new MovieDetailDTO(
                            update.getId(),
                            update.getTitle(),
                            update.getDuration(),
                            update.getMovieGenre(),
                            update.getDirector(),
                            update.getAgeRating(),
                            update.getSynopsis(),
                            update.getFunctions().stream()
                                    .map(Function::getId)
                                    .toList()
                    );
                })
                .orElseThrow(()-> new NotFoundException("La pelicula con ID: "+id+" no fue encontrada."));
    }


    public void deleteById(Long id){
        if(!movieRepository.existsById(id)){
            throw new NotFoundException("La pelicula con ID: "+id+" no fue encontrada.");
        }
        movieRepository.deleteById(id);
    }





}






