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

import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private IFunctionRepository functionRepository;


    public MovieDetailDTO create(MovieRequestDTO req) {
        // Validación de campos del DTO
        MovieValidator.CamposValidator(req);

        // Validación de existencia por título
        if (movieRepository.existsByTitle(req.getTitle().trim())) {
            throw new BadRequestException("Ya existe una película con el título: " + req.getTitle());
        }

        // Obtener la función asociada
        Function function = functionRepository.findById(req.getFunctionId())
                .orElseThrow(() -> new BadRequestException("No existe la función con ID " + req.getFunctionId()));

        // Crear nueva entidad Movie
        Movie movie = new Movie();
        movie.setTitle(req.getTitle().trim());
        movie.setDuration(req.getDuration());
        movie.setGenre(req.getGenre());
        movie.setDirector(req.getDirector());
        movie.setRating(req.getRating());
        movie.setSynopsis(req.getSynopsis());
        movie.getFunctionList().add(function); // Según cómo estés modelando la relación

        // Guardar
        Movie saved = movieRepository.save(movie);

        // Devolver DTO
        return new MovieDetailDTO(saved.getId(), saved.getTitle(), function.getId());
    }


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
                        movie.getDuration(),
                        movie.getGenre(),
                        movie.getDirector(),
                        movie.getRating(),
                        movie.getSynopsis()
                ))
                .toList();
    }

    public MovieDetailDTO findById(Long id){
        Movie m = movieRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("crear excepcion"));
        return new MovieDetailDTO(
                m.getId(),
                m.getTitle()
              //  m.getFunctionList()
        );
    }

    public MovieDetailDTO updateById(Long id, MovieRequestDTO entity){
        return movieRepository.findById(id).
                map(movie -> {
                    movie.setTitle(entity.getTitle());
                    movie.setDuration(entity.getDuration());
                    movie.setGenre(entity.getGenre());
                    movie.setDirector(entity.getDirector());
                    movie.setRating(entity.getRating());
                    movie.setSynopsis(entity.getSynopsis());
                   // movie.setFunctionId(entity.getFunctionId());

                    Movie update = movieRepository.save(movie);

                    return MovieDetailDTO(
                            update.getId(),
                            update.getTitle()
                           // update.getFunction().getId()
                    );
                }).orElseThrow(()-> new NotFoundException("No existe esa pelicula"));
    }

    public void deleteById(Long id){
        if(!movieRepository.existsById(id)){
            throw new NotFoundException("No existe esa pelicula.");
        }

        movieRepository.deleteById(id);
    }}






