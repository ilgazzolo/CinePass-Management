package com.api.boleteria.service;

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

@Service
@RequiredArgsConstructor
public class FunctionService {

    private final IFunctionRepository functionRepo;

    private final ICinemaRepository cinemaRepo;

    private final IMovieRepository movieRepo;

    /**
     * crea una nueva funcion
     * @param entity FunctionRequest de la nueva funcion
     * @return Function Detail
     */
    public FunctionDetailDTO create (FunctionRequestDTO entity){
        //  valida los campos ingresados
        FunctionValidator.validate(entity);

        //valida que no haya una funcion en esa sala en esa fecha
        if (functionRepo.existsByCinemaIdAndDate(entity.getCinemaId(), entity.getDate())) {
            throw new BadRequestException("Ya existe una función para esa sala en ese horario.");
        }

        //  verifica que no se creen funciones para dentro de mas de dos años
        if (entity.getDate().isAfter(LocalDateTime.now().plusYears(2))) {
            throw new BadRequestException("La fecha de la función es demasiado lejana.");
        }

        //  validaciones de sala
        Cinema cinema = cinemaRepo.findById(entity.getCinemaId()).orElseThrow(() -> new NotFoundException("No existe la sala ingresada."));
        if(!cinema.getEnabled()){
            throw new BadRequestException("La sala" + cinema.getName() + "ingresar no esta habilitada");
        }

        // validaciones pelicula
        Movie movie = movieRepo.findById(entity.getMovieId()).orElseThrow(() -> new NotFoundException("No existe la pelicula ingresada.") );

        // Verifica que no haya funciones en el rango horario
        List<Function> funcionesEnSala = functionRepo.findByCinemaId(entity.getCinemaId());
        FunctionValidator.validateHorario(entity, movie, funcionesEnSala);

        Function function = new Function();
        function.setShowtime(entity.getDate());
        function.setCinema(cinema);
        function.setAvailableCapacity(cinema.getSeatCapacity());
        function.setMovie(movie);


        Function saved = functionRepo.save(function);
        movie.getFunctions().add(saved);
        cinema.getFunctions().add(saved);

        return new FunctionDetailDTO(
                saved.getId(),
                saved.getShowtime().format(DateTimeFormatter.ISO_DATE_TIME),
                cinema.getRoomId(),
                movie.getId(),
                movie.getTitle()
        );
    }


    /**
     * muestra todas las funciones
     * @return Lista de FunctionList
     */
    public List<FunctionListDTO> findAll(){
        return functionRepo.findAll().stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getShowtime().toLocalDate(),
                        f.getShowtime().toLocalTime(),
                        f.getCinema().getRoomId(),
                        f.getMovie().getTitle(),
                        f.getAvailableCapacity()
                ))
                .toList();
    }


    /**
     * muestra funciones por id
     * @param id de la funcion a buscar
     * @return
     */
    public FunctionDetailDTO findById(Long id){
        Function function = functionRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("La funcion con ID: " + id + " no fue encontrada."));

        return new FunctionDetailDTO(
                function.getId(),
                function.getShowtime().format(DateTimeFormatter.ISO_DATE_TIME),
                function.getCinema().getRoomId(),
                function.getMovie().getId(),
                function.getMovie().getTitle()
        );
    }


    /**
     * actualiza una funcion segun el ID especificado
     * @param id de la funcion a modificar
     * @param entity objeto con los campos modificados
     * @return Function Detail
     */
    public FunctionDetailDTO updateById (Long id, FunctionRequestDTO entity){
        FunctionValidator.validate(entity);
        return functionRepo.findById(id)
                .map(f -> {
                    f.setShowtime(entity.getDate());

                    Cinema cinema = cinemaRepo.findById(entity.getCinemaId())
                            .orElseThrow(() -> new NotFoundException("La sala no fue encontrada."));
                    f.setCinema(cinema);

                    Movie movie = movieRepo.findById(entity.getMovieId())
                            .orElseThrow(() -> new NotFoundException("La pelicula no fue encontrada."));
                    f.setMovie(movie);

                    Function created = functionRepo.save(f);
                    return new FunctionDetailDTO(
                            created.getId(),
                            created.getShowtime().format(DateTimeFormatter.ISO_DATE_TIME),
                            created.getCinema().getRoomId(),
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
     * @return Lista de FunctionListDTO
     */
    public List<FunctionListDTO> findByMovieIdAndAvailableCapacity(Long movieId) {
        List<Function> funciones = functionRepo.findByMovieIdAndAvailableCapacityGreaterThanAndDateAfter(
                movieId, 0, LocalDateTime.now()
        );

        return funciones.stream()
                .map(f -> new FunctionListDTO(
                        f.getId(),
                        f.getShowtime().toLocalDate(),
                        f.getShowtime().toLocalTime(),
                        f.getCinema().getRoomId(),
                        f.getMovie().getTitle(),
                        f.getAvailableCapacity()
                ))
                .toList();
    }


    /**
     * muestra las funciones segun un tipo de pantalla especificado
     * @param screenType tipo de pantalla especificado
     * @return Lista de Funciones
     */
    public List<FunctionListDTO> findByTipoPantalla(ScreenType screenType) {
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
                        f.getCinema().getRoomId(),
                        f.getMovie().getTitle(),
                        f.getAvailableCapacity()
                ))
                .toList();

    }
}
