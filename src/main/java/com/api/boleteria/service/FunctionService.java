package com.api.boleteria.service;

import com.api.boleteria.dto.FunctionDetailDTO;
import com.api.boleteria.dto.FunctionListDTO;
import com.api.boleteria.dto.FunctionRequestDTO;
import com.api.boleteria.model.Function;
import com.api.boleteria.repository.IFunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FunctionService {
    private final IFunctionRepository functionRepo;
    //private final ICinemaRepository cinemaRepo;
    //private final IMovieRepository movieRepo;

    public FunctionDetailDTO create (FunctionRequestDTO entity){
        Function function = new Function();
        function.setDate(LocalDateTime.now());

        //Cinema cinema = cinemaRepo.findById(entity.getCinemaId());
        //function.setCinema(cinema);

        //Movie movie = movieRepo.findById(entity.getMovieId());
        //function.setMovie(movie);

        Function saved = functionRepo.save(function);

        return new FunctionDetailDTO(
                saved.getId(),
                saved.getDate(),
/*                cinema.getId(),
                movie.getId(),
                movie.getName()*/
        );
    }

    public List<FunctionListDTO> findAll(){
        return functionRepo.findAll().stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getDate().toLocalDate(),
                        f.getDate().toLocalTime(),
                        /*f.getCinema().getId(),
                        f.getMovie().getName()
                */))
                .toList();
    }

    public FunctionDetailDTO findById(Long id){
        Function function = functionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("crear exception 'NotFoundException' "+id));
        return new FunctionDetailDTO(
                function.getId(),
                function.getDate().format(DateTimeFormatter.ISO_DATE_TIME),
                /* function.getCinema.getId(),
                function.getMovie.getId(),
                function.getMovie.getName()*/
        );
    }

    public FunctionDetailDTO updateById (Long id, FunctionRequestDTO entity){
        return functionRepo.findById(id)
                .map(f -> {
                    f.setDate(entity.getDate());
                    /*f.setCinema(entity.getCinema());
                    f.setMovie(entity.getMovie());*/
                    Function created = functionRepo.save(f);
                    return new FunctionDetailDTO(
                            created.getId(),
                            created.getDate().format(DateTimeFormatter.BASIC_ISO_DATE),
                            /* created.getCinema.getId(),
                            created.getMovie.getId(),
                            created.getMovie.getName()*/
                    );
                })
                .orElseThrow(() -> new RuntimeException("crear exception 'NotFoundException' "+id));
    }

    public void deleteById (Long id){
        if (!functionRepo.existsById(id)){
            throw new RuntimeException("lanzar personalizada"+id);
        }
        functionRepo.deleteById(id);
    }
}
