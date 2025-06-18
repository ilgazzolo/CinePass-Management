package com.api.boleteria.service;


import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.ScreenType;
import com.api.boleteria.repository.ICinemaRepository;
import com.api.boleteria.repository.IFunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final ICinemaRepository cinemaRepository;

    private final IFunctionRepository functionRepository;

    /**
     * crea un nueva sala
     * @param entity CinemaRequests de la nueva sala
     * @return Cinema Detail
     */
    public CinemaDetailDTO save(CinemaRequestDTO entity){
        Cinema cinema = new Cinema();
        cinema.setName(entity.getNombre());
        cinema.setScreenType(entity.getScreenType());
        cinema.setAtmos(entity.getAtmos());
        cinema.setSeatCapacity(entity.getCapacity());
        cinema.setEnabled(entity.getHabilitada());

        Cinema saved = cinemaRepository.save(cinema);

        return new CinemaDetailDTO(
                saved.getRoomId(),
                saved.getName(),
                saved.getScreenType(),
                saved.getAtmos(),
                saved.getSeatCapacity(),
                saved.getEnabled()
        );
    }


    public List<CinemaListDTO> findAll(){
        return cinemaRepository.findAll().stream().
                map(c -> new CinemaListDTO(
                        c.getRoomId(),
                        c.getName(),
                        c.getSeatCapacity(),
                        c.getEnabled()
                ))
                .toList();
    }

    public CinemaDetailDTO findById(Long id){
        Cinema cinema = cinemaRepository.findById(id).
                orElseThrow(() -> new NotFoundException("La sala con ID: "+id+" no fue encontrada. "));

        return new CinemaDetailDTO(
                cinema.getRoomId(),
                cinema.getName(),
                cinema.getScreenType(),
                cinema.getAtmos(),
                cinema.getSeatCapacity(),
                cinema.getEnabled()
        );
    }

    
    public List<CinemaListDTO> findByScreenType(ScreenType screenType){
        return cinemaRepository.findByScreenType(screenType).stream()
                .map(p->new CinemaListDTO(
                        p.getRoomId(),
                        p.getName(),
                        p.getSeatCapacity(),
                        p.getEnabled()
                ))
                .toList();
    }


    public List<CinemaListDTO> findByEnabledRoom(boolean enabled){
        return cinemaRepository.findByEnabled(enabled).stream()
                .map(c->new CinemaListDTO(
                        c.getRoomId(),
                        c.getName(),
                        c.getSeatCapacity(),
                        c.getEnabled()
                ))
                .toList();
    }


    public List<CinemaListDTO> findBySeatCapacity(Integer seatCapacity){
        return cinemaRepository.findBySeatCapacityGreaterThan(0).stream()
                .map(c->new CinemaListDTO(
                        c.getRoomId(),
                        c.getName(),
                        c.getSeatCapacity(),
                        c.getEnabled()
                ))
                .toList();
    }


    public CinemaDetailDTO updateById(Long id, CinemaRequestDTO entity){
        return cinemaRepository.findById(id).
                map(c -> {
                    c.setSeatCapacity(entity.getCapacity());
                    Cinema created = cinemaRepository.save(c);
                    return new CinemaDetailDTO(
                            created.getRoomId(),
                            created.getName(),
                            created.getScreenType(),
                            created.getAtmos(),
                            created.getSeatCapacity(),
                            created.getEnabled()
                    );
                }).
                orElseThrow(() -> new NotFoundException("La sala con ID: "+id+" no fue encontrada. "));
    }


    public void deleteById (Long id){
        if (!cinemaRepository.existsById(id)){
            throw new NotFoundException("La sala con ID: "+id+" no fue encontrada. ");
        }
        cinemaRepository.deleteById(id);
    }

}
