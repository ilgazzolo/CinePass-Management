package com.api.boleteria.service;

import com.api.boleteria.dto.detail.MovieDetailDTO;
import com.api.boleteria.dto.list.MovieListDTO;
import com.api.boleteria.dto.request.MovieRequestDTO;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.Movie;
import com.api.boleteria.repository.IFunctionRepository;
import com.api.boleteria.repository.IMovieRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MovieService {

    @Autowired
    private IMovieRepository movieRepository;

    @Autowired
    private IFunctionRepository functionRepository;

    public MovieDetailDTO create (MovieRequestDTO req){
        Movie movie = new Movie();
        movie.setTitle();

        Function function = functionRepository.findById(req.getFunctionId()).orElseThrow();
        movie.setFunction(function);

        Movie newMovie = movieRepository.save(movie);

        return new MovieDetailDTO(
                newMovie.getId(),
                newMovie.getTitle(),
                function.getId()
        );

    }



    public List<MovieListDTO> findAll(){
        return movieRepository.findAll().stream().
                map(movie -> new MovieListDTO(
                        movie.getId(),
                        movie.getTitle(),
                        movie.getDuration(),
                        movie.getGenre(),
                        movie.getDirector(),
                        movie.getRating(),
                        movie.getSynopsis(),
                        movie.getFunctionId()
                ))
                .toList();
    }

    public MovieDetailDTO findById(Long id){
        Movie m = movieRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("crear excepcion"));
        return new MovieDetailDTO(
                m.getId(),
                m.getTitle(),
                m.getFunction().getId()
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
                    movie.setFunctionId(entity.getFunctionId());

                    Movie update = movieRepository.save(movie);

                    return MovieDetailDTO(
                            update.getId(),
                            update.getTitle(),
                            update.getFunction().getId()
                    );
                }).orElseThrow(()-> new RuntimeException("crear excepcion"));
    }

    public void deleteById(Long id){
        if(!movieRepository.existsById(id)){
            throw new RuntimeException("not found");
        }

        movieRepository.deleteById(id);
    }




}
