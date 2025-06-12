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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovieService {

    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private IFunctionRepository functionRepository;

    /*
    public MovieDetailDTO create(MovieRequestDTO req) {
        // Validación de campos del DTO
        MovieValidator.CamposValidator(req);

        // Validación de existencia por título
        if (movieRepository.existsByTitle(req.getTitle().trim())) {
            throw new BadRequestException("Ya existe una película con el título: " + req.getTitle());
        }

        List<Function> functionList = functionRepository.findAllById(req.getFunctionListId());

        Set<Long> providedIds = new HashSet<>(req.getFunctionListId());
        Set<Long> foundIds = functionList.stream()
                .map(Function::getId)
                .collect(Collectors.toSet());

        providedIds.removeAll(foundIds);

        if (!providedIds.isEmpty()) {
            throw new NotFoundException("this functions doesn't exist: " + providedIds);
        }

        // Crear nueva entidad Movie
        Movie movie = new Movie();
        movie.setTitle(req.getTitle().trim());
        movie.setMin(req.getMin());
        movie.setGenre(req.getGenre());
        movie.setDirector(req.getDirector());
        movie.setRating(req.getRating());
        movie.setSynopsis(req.getSynopsis());
        movie.setFunctionList(functionList);

        // Guardar
        Movie saved = movieRepository.save(movie);

        // Devolver DTO
        return new MovieDetailDTO(
                saved.getId(),
                saved.getTitle(),
                saved.getMin(),
                saved.getGenre(),
                saved.getDirector(),
                saved.getRating(),
                saved.getSynopsis(),
                saved.getFunctionList().stream()
                        .map(Function::getId)
                        .toList()
                );

    }
    */



    /*
    public boolean movieExistsByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío.");
        }
        return movieRepository.existsByTitle(title.trim());
    }

     */



    public List<MovieListDTO> findAll(){
        return movieRepository.findAll().stream().
                map(movie -> new MovieListDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getMin(),
                        movie.getGenre(),
                        movie.getDirector()
                ))
                .toList();
    }

    public MovieDetailDTO findById(Long id){
        Movie m = movieRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("doesn't exist movie ID: "+id));
        return new MovieDetailDTO(
                m.getId(),
                m.getTitle(),
                m.getMin(),
                m.getGenre(),
                m.getDirector(),
                m.getRating(),
                m.getSynopsis(),
                m.getFunctionList().stream()
                        .map(Function::getId)
                        .toList()
        );
    }

    public MovieDetailDTO updateById(Long id, MovieRequestDTO entity){
        return movieRepository.findById(id).
                map(movie -> {
                    movie.setTitle(entity.getTitle());
                    movie.setMin(entity.getMin());
                    movie.setGenre(entity.getGenre());
                    movie.setDirector(entity.getDirector());
                    movie.setRating(entity.getRating());
                    movie.setSynopsis(entity.getSynopsis());

                    List<Function>functions = functionRepository.findAllById(entity.getFunctionListId());

                    movie.setFunctionList(functions);

                    Movie update = movieRepository.save(movie);

                    return new MovieDetailDTO(
                            update.getId(),
                            update.getTitle(),
                            update.getMin(),
                            update.getGenre(),
                            update.getDirector(),
                            update.getRating(),
                            update.getSynopsis(),
                            update.getFunctionList().stream()
                                    .map(Function::getId)
                                    .toList()
                    );
                })
                .orElseThrow(()-> new NotFoundException("doesn't exist movie ID: "+id));
    }

    public void deleteById(Long id){
        if(!movieRepository.existsById(id)){
            throw new NotFoundException("doesn't exist movie ID: "+id);
        }
        movieRepository.deleteById(id);
    }
}






