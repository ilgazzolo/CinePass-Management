package com.api.boleteria.service;

import com.api.boleteria.dto.detail.FunctionDetailDTO;
import com.api.boleteria.dto.list.FunctionListDTO;
import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.Movie;
import com.api.boleteria.repository.ICinemaRepository;
import com.api.boleteria.repository.IFunctionRepository;
import com.api.boleteria.repository.IMovieRepository;
import com.api.boleteria.validators.FunctionValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FunctionService {
    private final IFunctionRepository functionRepo;
    private final ICinemaRepository cinemaRepo;
    private final IMovieRepository movieRepo;

    public FunctionDetailDTO create (FunctionRequestDTO entity){
        ///  valida los campos ingresados
        FunctionValidator.validate(entity);

        /// valida que no haya una funcion en esa sala en esa fecha
        if (functionRepo.existsByCinemaIdAndDate(entity.getCinemaId(), entity.getDate())) {
            throw new BadRequestException("Ya existe una función para esa sala en ese horario.");
        }

        ///  verifica que no se creen funciones para dentro de mas de dos años
        if (entity.getDate().isAfter(LocalDateTime.now().plusYears(2))) {
            throw new BadRequestException("La fecha de la función es demasiado lejana.");
        }
        Function function = new Function();
        function.setDate(entity.getDate());
        Cinema cinema = cinemaRepo.findById(entity.getCinemaId()).orElseThrow(() -> new NotFoundException("No existe la sala ingresada."));
        function.setCinema(cinema);

        function.setCapacidadDisponible(cinema.getCapacity());

        Movie movie = movieRepo.findById(entity.getMovieId()).orElseThrow(() -> new NotFoundException("No existe la pelicula ingresada.") );
        function.setMovie(movie);

        ///  Verifica que no haya funciones en el rango horario
        List<Function> funcionesEnSala = functionRepo.findByCinemaId(entity.getCinemaId());
        FunctionValidator.validateHorario(entity, movie, funcionesEnSala);

        Function saved = functionRepo.save(function);

        return new FunctionDetailDTO(
                saved.getId(),
                saved.getDate().format(DateTimeFormatter.ISO_DATE_TIME),
                cinema.getId(),
                movie.getId(),
                movie.getTitle()
        );
    }

    ///  ver todas las funciones
    public List<FunctionListDTO> findAll(){
        return functionRepo.findAll().stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getDate().toLocalDate(),
                        f.getDate().toLocalTime(),
                        f.getCinema().getId(),
                        f.getMovie().getTitle()
                ))
                .toList();
    }

    ///  ver una funcion por id
    public FunctionDetailDTO findById(Long id){
        Function function = functionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("crear exception 'NotFoundException' "+id));
        return new FunctionDetailDTO(
                function.getId(),
                function.getDate().format(DateTimeFormatter.ISO_DATE_TIME),
                function.getCinema().getId(),
                function.getMovie().getId(),
                function.getMovie().getTitle()
        );
    }

    ///  actualizar una funcion por id
    public FunctionDetailDTO updateById (Long id, FunctionRequestDTO entity){
        FunctionValidator.validate(entity);
        return functionRepo.findById(id)
                .map(f -> {
                    f.setDate(entity.getDate());

                    Cinema cinema = cinemaRepo.findById(entity.getCinemaId())
                            .orElseThrow(() -> new NotFoundException("Cinema not found"));
                    f.setCinema(cinema);

                    Movie movie = movieRepo.findById(entity.getMovieId())
                            .orElseThrow(() -> new NotFoundException("Movie not found"));
                    f.setMovie(movie);

                    Function created = functionRepo.save(f);
                    return new FunctionDetailDTO(
                            created.getId(),
                            created.getDate().format(DateTimeFormatter.ISO_DATE_TIME),
                            created.getCinema().getId(),
                            created.getMovie().getId(),
                            created.getMovie().getTitle()
                    );
                })
                .orElseThrow(() -> new NotFoundException("not found ID function: "+id));
    }

    ///  eliminar una funcion por id
    public void deleteById (Long id){
        if (!functionRepo.existsById(id)){
            throw new NotFoundException("not found ID function: "+id);
        }
        functionRepo.deleteById(id);
    }

    ///  filtrar funciones por pelicula
    public List<FunctionListDTO> findAvailableByMovieId(Long movieId) {
        List<Function> funciones = functionRepo.findByMovieIdAndCapacidadDisponibleGreaterThanAndDateAfter(
                movieId, 0, LocalDateTime.now()
        );

        return funciones.stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getDate().toLocalDate(),
                        f.getDate().toLocalTime(),
                        f.getCinema().getId(),
                        f.getMovie().getTitle()
                ))
                .toList();
    }



}
