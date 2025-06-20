package com.api.boleteria.service;

import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.ScreenType;
import com.api.boleteria.repository.ICinemaRepository;
import com.api.boleteria.repository.IFunctionRepository;
import com.api.boleteria.validators.CinemaValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestionar operaciones relacionadas con Salas de Cine.
 */
@Service
@RequiredArgsConstructor
public class CinemaService {

    private final ICinemaRepository cinemaRepository;

    private final IFunctionRepository functionRepository;

    /**
     * Convierte una entidad Cinema en un DTO de detalle.
     * @param cinema entidad Cinema a convertir
     * @return CinemaDetailDTO con los datos detallados de la sala
     */
    private CinemaDetailDTO mapToDetailDTO(Cinema cinema) {
        return new CinemaDetailDTO(
                cinema.getId(),
                cinema.getName(),
                cinema.getScreenType(),
                cinema.getAtmos(),
                cinema.getSeatCapacity(),
                cinema.getEnabled()
        );
    }

    /**
     * Convierte una entidad Cinema en un DTO de listado.
     * @param cinema entidad Cinema a convertir
     * @return CinemaListDTO con los datos resumidos de la sala
     */
    private CinemaListDTO mapToListDTO(Cinema cinema) {
        return new CinemaListDTO(
                cinema.getId(),
                cinema.getName(),
                cinema.getSeatCapacity(),
                cinema.getEnabled()
        );
    }

    /**
     * Crea una o más salas (cinemas) a partir de una lista de DTOs.
     *
     * @param requests Lista de DTOs con los datos de cada sala a crear.
     * @return Lista de CinemaDetailDTO con la información de las salas creadas.
     */
    public List<CinemaDetailDTO> saveAll(List<CinemaRequestDTO> requests) {
        List<Cinema> cinemasToSave = new ArrayList<>();

        for (CinemaRequestDTO dto : requests) {
            CinemaValidator.validateFields(dto);

            Cinema cinema = new Cinema();
            cinema.setName(dto.getNombre());
            cinema.setScreenType(dto.getScreenType());
            cinema.setAtmos(dto.getAtmos());
            cinema.setSeatCapacity(dto.getCapacity());
            cinema.setEnabled(dto.getEnabled());

            cinemasToSave.add(cinema);
        }

        List<Cinema> savedCinemas = cinemaRepository.saveAll(cinemasToSave);

        return savedCinemas.stream()
                .map(this::mapToDetailDTO)
                .toList();
    }

    /**
     * obtiene todas las salas cargadas
     * @return lista de CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findAll() {
        return cinemaRepository.findAll().stream()
                .map(this::mapToListDTO)
                .toList();
    }

    /**
     * obtiene una sala segun un ID especificado
     * @param id ID de la sala a buscar
     * @return SalaDetail con la informacion de la sala encontrada
     */
    public CinemaDetailDTO findById(Long id) {
        Cinema cinema = cinemaRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("La sala con ID: " + id + " no fue encontrada. "));
        return mapToDetailDTO(cinema);
    }

    /**
     * muestra todas las salas con un tipo de pantalla en especifico
     * @param screenType tipo de pantalla de la sala a mostrar
     * @return lista de CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findByScreenType(ScreenType screenType) {
        return cinemaRepository.findByScreenType(screenType).stream()
                .map(this::mapToListDTO)
                .toList();
    }

    /**
     * obtiene las salas segun un estado especificado
     * @param enabled estado de las salas a mostrar
     * @return CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findByEnabledRoom(boolean enabled) {
        return cinemaRepository.findByEnabled(enabled).stream()
                .map(this::mapToListDTO)
                .toList();
    }

    /**
     * obtiene las salas cuya capacidad de asientos sea mayor al valor recibido como parámetro.
     * @param seatCapacity capacidad de asientos especificada
     * @return CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findBySeatCapacity(Integer seatCapacity) {
        return cinemaRepository.findBySeatCapacityGreaterThan(seatCapacity).stream()
                .map(this::mapToListDTO)
                .toList();
    }

    /**
     * actualiza una sala, segun un ID especificado
     * @param id ID de la sala a actualizar
     * @param entity DTO con los cambios realizados
     * @return CinemaDetail con la informacion de la sala actualizada
     */
    public CinemaDetailDTO updateById(Long id, CinemaRequestDTO entity) {
        CinemaValidator.validateFields(entity);
        return cinemaRepository.findById(id)
                .map(c -> {
                    c.setSeatCapacity(entity.getCapacity());
                    Cinema updated = cinemaRepository.save(c);
                    return mapToDetailDTO(updated);
                })
                .orElseThrow(() -> new NotFoundException("La sala con ID: " + id + " no fue encontrada. "));
    }

    /**
     * elimina una sala segun un ID especificado
     * @param id ID de la sala a eliminar
     */
    public void deleteById(Long id) {
        if (!cinemaRepository.existsById(id)) {
            throw new NotFoundException("La sala con ID: " + id + " no fue encontrada. ");
        }
        cinemaRepository.deleteById(id);
    }
}

