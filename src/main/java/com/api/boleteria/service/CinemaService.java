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
     * crea un nueva sala
     * @param entity DTO con la informacion de la nueva sala
     * @return Cinema Detail con la informacion de la sala creada
     */
    public CinemaDetailDTO save(CinemaRequestDTO entity){
        CinemaValidator.validateFields(entity);

        Cinema cinema = new Cinema();
        cinema.setName(entity.getNombre());
        cinema.setScreenType(entity.getScreenType());
        cinema.setAtmos(entity.getAtmos());
        cinema.setSeatCapacity(entity.getCapacity());
        cinema.setEnabled(entity.getHabilitada());

        Cinema saved = cinemaRepository.save(cinema);

        return new CinemaDetailDTO(
                saved.getId(),
                saved.getName(),
                saved.getScreenType(),
                saved.getAtmos(),
                saved.getSeatCapacity(),
                saved.getEnabled()
        );
    }


    /**
     * obtiene todas las salas cargadas
     * @return lista de CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findAll(){
        return cinemaRepository.findAll().stream().
                map(c -> new CinemaListDTO(
                        c.getId(),
                        c.getName(),
                        c.getSeatCapacity(),
                        c.getEnabled()
                ))
                .toList();
    }


    /**
     * obtiene una sala segun un ID especificado
     * @param id ID de la sala a buscar
     * @return SalaDetail con la informacion de la sala encontrada
     */
    public CinemaDetailDTO findById(Long id){
        Cinema cinema = cinemaRepository.findById(id).
                orElseThrow(() -> new NotFoundException("La sala con ID: "+id+" no fue encontrada. "));

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
     * mustra todas las salas con un tipo de pantlla en especifico
     * @param screenType tipo de pantalla de la sala a mostrar
     * @return lista de CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findByScreenType(ScreenType screenType){
        return cinemaRepository.findByScreenType(screenType).stream()
                .map(p->new CinemaListDTO(
                        p.getId(),
                        p.getName(),
                        p.getSeatCapacity(),
                        p.getEnabled()
                ))
                .toList();
    }


    /**
     * obtiene las salas segun un estado especificado
     * @param enabled estado de las salas a mostrar
     * @return CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findByEnabledRoom(boolean enabled){
        return cinemaRepository.findByEnabled(enabled).stream()
                .map(c->new CinemaListDTO(
                        c.getId(),
                        c.getName(),
                        c.getSeatCapacity(),
                        c.getEnabled()
                ))
                .toList();
    }


    /**
     * obtiene las salas cuya capacidad de asientos sea mayor al valor recibido como parámetro.
     * @param seatCapacity capacidad de asientos especificada
     * @return CinemaList con la informacion de las salas encontradas
     */
    public List<CinemaListDTO> findBySeatCapacity(Integer seatCapacity){
        return cinemaRepository.findBySeatCapacityGreaterThan(0).stream()
                .map(c->new CinemaListDTO(
                        c.getId(),
                        c.getName(),
                        c.getSeatCapacity(),
                        c.getEnabled()
                ))
                .toList();
    }


    /**
     * actualiza una sala, segun un ID especificado
     * @param id ID de la sala a actualizar
     * @param entity DTO con los cambios realizados
     * @return CinemaDetail con la informacion de la sala actualizada
     */
    public CinemaDetailDTO updateById(Long id, CinemaRequestDTO entity){
       CinemaValidator.validateFields(entity);
        return cinemaRepository.findById(id).
                map(c -> {
                    c.setSeatCapacity(entity.getCapacity());
                    Cinema created = cinemaRepository.save(c);
                    return new CinemaDetailDTO(
                            created.getId(),
                            created.getName(),
                            created.getScreenType(),
                            created.getAtmos(),
                            created.getSeatCapacity(),
                            created.getEnabled()
                    );
                }).
                orElseThrow(() -> new NotFoundException("La sala con ID: "+id+" no fue encontrada. "));
    }


    /**
     * elimina una sala segun un ID especficado
     * @param id ID de la sala a eliminar
     */
    public void deleteById (Long id){
        if (!cinemaRepository.existsById(id)){
            throw new NotFoundException("La sala con ID: "+id+" no fue encontrada. ");
        }
        cinemaRepository.deleteById(id);
    }

}
