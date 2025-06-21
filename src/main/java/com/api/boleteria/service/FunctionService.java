package com.api.boleteria.service;

import com.api.boleteria.dto.detail.FunctionDetailDTO;
import com.api.boleteria.dto.list.FunctionListDTO;
import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.exception.BadRequestException;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.enums.ScreenType;
import com.api.boleteria.model.Function;
import com.api.boleteria.model.Movie;
import com.api.boleteria.repository.ICinemaRepository;
import com.api.boleteria.repository.IFunctionRepository;
import com.api.boleteria.repository.IMovieRepository;
import com.api.boleteria.validators.CinemaValidator;
import com.api.boleteria.validators.FunctionValidator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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


    //-------------------------------SAVE--------------------------------//

    /**
     * Crea una o varias funciones, validando para cada una que no exista función en la misma sala y horario,
     * que la fecha no supere los dos años, y que no haya solapamientos.
     *
     * @param entities Lista de DTOs con la información de las nuevas funciones.
     * @return Lista de FunctionDetailDTO con la información de las funciones creadas.
     * @throws BadRequestException si alguna función no cumple las validaciones.
     * @throws NotFoundException si alguna sala o película no existe.
     */
    @Transactional
    public List<FunctionDetailDTO> createAll(List<FunctionRequestDTO> entities) {
        List<FunctionDetailDTO> createdFunctions = new ArrayList<>();

        for (FunctionRequestDTO entity : entities) {
            FunctionValidator.validateFields(entity);

            if (functionRepo.existsByCinemaIdAndShowtime(entity.getCinemaId(), entity.getShowtime())) {
                throw new BadRequestException("Ya existe una función para la sala " + entity.getCinemaId() + " en el horario " + entity.getShowtime());
            }

            FunctionValidator.validateMaxTwoYears(entity);

            Cinema cinema = cinemaRepo.findById(entity.getCinemaId())
                    .orElseThrow(() -> new NotFoundException("No existe la sala con ID: " + entity.getCinemaId()));
            FunctionValidator.validateEnabledCinema(cinema);

            Movie movie = movieRepo.findById(entity.getMovieId())
                    .orElseThrow(() -> new NotFoundException("No existe la película con ID: " + entity.getMovieId()));

            List<Function> functionsInTheCinema = functionRepo.findByCinemaId(entity.getCinemaId());
            FunctionValidator.validateSchedule(entity, movie, functionsInTheCinema);

            Function function = mapToEntity(entity, cinema, movie);

            Function saved = functionRepo.save(function);
            movie.getFunctions().add(saved);
            cinema.getFunctions().add(saved);

            createdFunctions.add(mapToDetailDTO(saved));
        }

        return createdFunctions;
    }




    //-------------------------------FIND--------------------------------//

    /**
     * muestra todas las funciones
     * @return Lista de FunctionList con la informacion de las funciones encontradas
     */
    public List<FunctionListDTO> findAll() {
        return functionRepo.findAll().stream()
                .map(this::mapToListDTO)
                .toList();
    }

    /**
     * obtiene las funciones segun un ID especificado
     * @param id de la funcion a buscar
     * @return Function Detail con la informacion de la funcion encontrada
     */
    public FunctionDetailDTO findById(Long id) {
        FunctionValidator.validateId(id);
        Function function = functionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("La funcion con ID: " + id + " no fue encontrada."));

        return mapToDetailDTO(function);
    }

    /**
     * Muestra solo las próximas funciones disponibles de una película, según su ID.
     *
     * Valida que el ID de la película sea mayor a 0 y que exista en la base de datos.
     * Solo se devuelven funciones con capacidad disponible y cuya fecha de inicio
     * sea posterior al momento actual.
     *
     * @param movieId ID de la película que se desea mostrar sus funciones.
     * @return Lista de FunctionListDTO con la información de las funciones encontradas.
     * @throws IllegalArgumentException si el ID proporcionado no es válido.
     * @throws NotFoundException si no existe una película con el ID especificado.
     */
    public List<FunctionListDTO> findByMovieIdAndAvailableCapacity(Long movieId) {
        FunctionValidator.validateMovieId(movieId);

        if (!movieRepo.existsById(movieId)) {
            throw new NotFoundException("La película con ID " + movieId + " no fue encontrada.");
        }

        List<Function> functions = functionRepo
                .findByMovieIdAndAvailableCapacityGreaterThanAndShowtimeAfter(
                        movieId, 0, LocalDateTime.now());

        return functions.stream()
                .map(this::mapToListDTO)
                .toList();
    }

    /**
     * muestra las funciones segun un tipo de pantalla especificado
     * @param screenType tipo de pantalla especificado
     * @return Lista de Funciones encontradas
     */
    public List<FunctionListDTO> findByScreenType(ScreenType screenType) {
        CinemaValidator.validateScreenType(screenType);

        List<Function> functions = functionRepo
                .findByCinema_ScreenTypeAndAvailableCapacityGreaterThanAndShowtimeAfter(
                        screenType, 0, LocalDateTime.now());


        return functions.stream()
                .map(this::mapToListDTO)
                .toList();
    }



    //-------------------------------UPDATE--------------------------------//
    /**
     * Actualiza una función según el ID especificado.
     *
     * @param id ID de la función a modificar.
     * @param entity Objeto DTO con los campos modificados.
     * @return FunctionDetailDTO con la información de la función actualizada.
     * @throws NotFoundException si la función, sala o película no existen.
     * @throws BadRequestException si hay conflictos de horario, validaciones o restricciones.
     */
    public FunctionDetailDTO updateById(Long id, FunctionRequestDTO entity) {
        FunctionValidator.validateId(id);
        FunctionValidator.validateFields(entity);
        FunctionValidator.validateMaxTwoYears(entity);

        Function function = functionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("La función con ID: " + id + " no fue encontrada."));

        if (functionRepo.existsByCinemaIdAndShowtime(entity.getCinemaId(), entity.getShowtime())) {
            throw new BadRequestException("Ya existe una función para esa sala en ese horario.");
        }

        Cinema cinema = cinemaRepo.findById(entity.getCinemaId())
                .orElseThrow(() -> new NotFoundException("No existe la sala ingresada."));
        FunctionValidator.validateEnabledCinema(cinema);

        Movie movie = movieRepo.findById(entity.getMovieId())
                .orElseThrow(() -> new NotFoundException("No existe la película ingresada."));

        List<Function> functionsInCinema = functionRepo.findByCinemaId(entity.getCinemaId());
        FunctionValidator.validateSchedule(entity, movie, functionsInCinema);

        function.setShowtime(entity.getShowtime());
        function.setCinema(cinema);
        function.setMovie(movie);
        function.setAvailableCapacity(cinema.getSeatCapacity());

        Function updated = functionRepo.save(function);
        return mapToDetailDTO(updated);
    }



    //-------------------------------DELETE--------------------------------//

    /**
     * elimina una funcion segun un ID especificado
     * @param id de la funcion a eliminar
     */
    public void deleteById(Long id) {
        if (!functionRepo.existsById(id)) {
            throw new NotFoundException("La funcion con ID: " + id + " no fue encontrada.");
        }
        functionRepo.deleteById(id);
    }



    //-------------------------------MAPS--------------------------------//

    /**
     * Convierte una entidad Function en un DTO de detalle.
     * @param function entidad Function
     * @return FunctionDetailDTO con los datos detallados de la función
     */
    private FunctionDetailDTO mapToDetailDTO(Function function) {
        return new FunctionDetailDTO(
                function.getId(),
                function.getShowtime().format(DateTimeFormatter.ISO_DATE_TIME),
                function.getCinema().getId(),
                function.getMovie().getId(),
                function.getMovie().getTitle()
        );
    }

    /**
     * Convierte una entidad Function en un DTO de lista.
     * @param function entidad Function
     * @return FunctionListDTO con los datos resumidos de la función
     */
    private FunctionListDTO mapToListDTO(Function function) {
        return new FunctionListDTO(
                function.getId(),
                function.getShowtime().toLocalDate(),
                function.getShowtime().toLocalTime(),
                function.getCinema().getId(),
                function.getMovie().getTitle(),
                function.getAvailableCapacity()
        );
    }

    private Function mapToEntity(FunctionRequestDTO entity, Cinema cinema, Movie movie) {
        Function function = new Function();
        function.setShowtime(entity.getShowtime());
        function.setCinema(cinema);
        function.setAvailableCapacity(cinema.getSeatCapacity());
        function.setMovie(movie);
        return function;
    }

}
