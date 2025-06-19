package com.api.boleteria.service;

import com.api.boleteria.controller.FunctionController;
import com.api.boleteria.dto.detail.FunctionDetailDTO;
import com.api.boleteria.dto.list.FunctionListDTO;
import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.ScreenType;
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
/**
 * Servicio para gestionar operaciones relacionadas con Funciones.
 */
@Service
@RequiredArgsConstructor
public class FunctionService {

    private final IFunctionRepository functionRepo;

    private final ICinemaRepository cinemaRepo;

    private final IMovieRepository movieRepo;

    /**
     * Crea una nueva funcion, validando que no exista una funcion en la misma sala en el horario especificado.
     *
     * Verifica que no se creen funciones con fecha de inicio mayor a dos años.
     * @param entity FunctionRequest con la informacion de la nueva funcion
     * @return Function Detail con la informacion de la funcion creada
     */
    public FunctionDetailDTO create (FunctionRequestDTO entity){
        //  valida los campos ingresados
        FunctionValidator.validateFields(entity);

        // valida que no haya una funcion en esa sala en esa fecha
        if (functionRepo.existsByCinemaIdAndShowtime(entity.getCinemaId(), entity.getShowtime())) {
            throw new BadRequestException("Ya existe una función para esa sala en ese horario.");
        }

        //  verifica que no se creen funciones para dentro de mas de dos años
       FunctionValidator.validateMaxTwoYears(entity);

        //  validaciones de sala
        Cinema cinema = cinemaRepo.findById(entity.getCinemaId()).orElseThrow(() -> new NotFoundException("No existe la sala ingresada."));
        FunctionValidator.validateEnabledCinema(cinema);


        // verifica que exista la pelicula
        Movie movie = movieRepo.findById(entity.getMovieId()).orElseThrow(() -> new NotFoundException("No existe la pelicula ingresada.") );

        // Verifica que no haya funciones en el rango horario
        List<Function> funcionesEnSala = functionRepo.findByCinemaId(entity.getCinemaId());
        FunctionValidator.validateHorario(entity, movie, funcionesEnSala);

        Function function = new Function();
        function.setShowtime(entity.getShowtime());
        function.setCinema(cinema);
        function.setAvailableCapacity(cinema.getSeatCapacity());
        function.setMovie(movie);


        Function saved = functionRepo.save(function);
        movie.getFunctions().add(saved);
        cinema.getFunctions().add(saved);

        return new FunctionDetailDTO(
                saved.getId(),
                saved.getShowtime().format(DateTimeFormatter.ISO_DATE_TIME),
                cinema.getId(),
                movie.getId(),
                movie.getTitle()
        );
    }


    /**
     * muestra todas las funciones
     * @return Lista de FunctionList con la informacion de las funciones encontradas
     */
    public List<FunctionListDTO> findAll(){
        return functionRepo.findAll().stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getShowtime().toLocalDate(),
                        f.getShowtime().toLocalTime(),
                        f.getCinema().getId(),
                        f.getMovie().getTitle(),
                        f.getAvailableCapacity()
                ))
                .toList();
    }


    /**
     * obtiene las funciones segun un ID especificado
     * @param id de la funcion a buscar
     * @return Function Detail con la informacion de la funcion encontrada
     */
    public FunctionDetailDTO findById(Long id){
        Function function = functionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("La funcion con ID: " + id + " no fue encontrada."));

        return new FunctionDetailDTO(
                function.getId(),
                function.getShowtime().format(DateTimeFormatter.ISO_DATE_TIME),
                function.getCinema().getId(),
                function.getMovie().getId(),
                function.getMovie().getTitle()
        );
    }


    /**
     * actualiza una funcion segun el ID especificado
     * @param id de la funcion a modificar
     * @param entity objeto DTO con los campos modificados
     * @return Function Detail con la informacion de la funcion actualizada
     */
    public FunctionDetailDTO updateById (Long id, FunctionRequestDTO entity){
        FunctionValidator.validateFields(entity);
        // valida que no haya una funcion en esa sala en esa fecha
        if (functionRepo.existsByCinemaIdAndShowtime(entity.getCinemaId(), entity.getShowtime())) {
            throw new BadRequestException("Ya existe una función para esa sala en ese horario.");
        }

        //  verifica que no se creen funciones para dentro de mas de dos años
        FunctionValidator.validateMaxTwoYears(entity);

        //  validaciones de sala
        Cinema cinema = cinemaRepo.findById(entity.getCinemaId()).orElseThrow(() -> new NotFoundException("No existe la sala ingresada."));
        FunctionValidator.validateEnabledCinema(cinema);


        // verifica que exista la pelicula
        Movie movie = movieRepo.findById(entity.getMovieId()).orElseThrow(() -> new NotFoundException("No existe la pelicula ingresada.") );

        // Verifica que no haya funciones en el rango horario
        List<Function> funcionesEnSala = functionRepo.findByCinemaId(entity.getCinemaId());
        FunctionValidator.validateHorario(entity, movie, funcionesEnSala);


        return functionRepo.findById(id)
                .map(f -> {
                    f.setShowtime(entity.getShowtime());
                    f.setCinema(cinema);
                    f.setMovie(movie);
                    f.setAvailableCapacity(cinema.getSeatCapacity());

                    Function created = functionRepo.save(f);
                    return new FunctionDetailDTO(
                            created.getId(),
                            created.getShowtime().format(DateTimeFormatter.ISO_DATE_TIME),
                            created.getCinema().getId(),
                            created.getMovie().getId(),
                            created.getMovie().getTitle()
                    );
                })
                .orElseThrow(() -> new NotFoundException("La funcion con ID: "+id+" no fue encontrada."));
    }


    /**
     * elimina una funcion segun un ID especificado
     * @param id de la funcion a eliminar
     */
    public void deleteById (Long id){
        if (!functionRepo.existsById(id)){
            throw new NotFoundException("La funcion con ID: "+id+" no fue encontrada.");
        }
        functionRepo.deleteById(id);
    }


    /**
     * muestra solo las proximas funciones de una pelicula segun su ID
     * @param movieId id de la pelicula que se desea mostrar sus funciones
     * @return Lista de FunctionListDTO con la informacion de las funciones encontradas
     */
    public List<FunctionListDTO> findByMovieIdAndAvailableCapacity(Long movieId) {
        List<Function> funciones = functionRepo.findByMovieIdAndAvailableCapacityGreaterThanAndShowtimeAfter(
                movieId, 0, LocalDateTime.now()
        );

        return funciones.stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getShowtime().toLocalDate(),
                        f.getShowtime().toLocalTime(),
                        f.getCinema().getId(),
                        f.getMovie().getTitle(),
                        f.getAvailableCapacity()
                ))
                .toList();
    }


    /**
     * muestra las funciones segun un tipo de pantalla especificado
     * @param screenType tipo de pantalla especificado
     * @return Lista de Funciones encontradas
     */
    public List<FunctionListDTO> findByScreenType(ScreenType screenType) {
        if (screenType == null) {
            throw new BadRequestException("Debe especificar un tipo de pantalla.");
        }

        List<Function> funciones = functionRepo
                .findByCinema_ScreenTypeAndAvailableCapacityGreaterThanAndShowtimeAfter(screenType, 0, LocalDateTime.now());

        if (funciones.isEmpty()) {
            throw new NotFoundException("No hay funciones disponibles para el tipo de pantalla: " + screenType.name());
        }

        return funciones.stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getShowtime().toLocalDate(),
                        f.getShowtime().toLocalTime(),
                        f.getCinema().getId(),
                        f.getMovie().getTitle(),
                        f.getAvailableCapacity()
                ))
                .toList();

    }
}
