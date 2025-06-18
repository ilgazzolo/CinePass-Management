package com.api.boleteria.service;


import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.TipoPantalla;
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

    public CinemaDetailDTO save(CinemaRequestDTO entity){
        Cinema cinema = new Cinema();
        cinema.setNombre(entity.getNombre());
        cinema.setTipoPantalla(entity.getTipoPantalla());
        cinema.setAtmos(entity.getAtmos());
        cinema.setCapacity(entity.getCapacity());
        cinema.setHabilitada(entity.getHabilitada());

        Cinema saved = cinemaRepository.save(cinema);

        return new CinemaDetailDTO(
                saved.getId(),
                saved.getNombre(),
                saved.getTipoPantalla(),
                saved.getAtmos(),
                saved.getCapacity(),
                saved.getHabilitada()
        );
    }


    public List<CinemaListDTO> findAll(){
        return cinemaRepository.findAll().stream().
                map(c -> new CinemaListDTO(
                        c.getId(),
                        c.getNombre(),
                        c.getCapacity(),
                        c.getHabilitada()
                ))
                .toList();
    }

    public CinemaDetailDTO findById(Long id){
        Cinema cinema = cinemaRepository.findById(id).
                orElseThrow(() -> new NotFoundException("doesn't exist movie ID: "+id));

        return new CinemaDetailDTO(
                cinema.getId(),
                cinema.getNombre(),
                cinema.getTipoPantalla(),
                cinema.getAtmos(),
                cinema.getCapacity(),
                cinema.getHabilitada()
        );
    }

    public List<CinemaListDTO> findByTipoPantalla(TipoPantalla tipoPantalla){
        return cinemaRepository.findByTipoPantalla(tipoPantalla).stream()
                .map(p->new CinemaListDTO(
                        p.getId(),
                        p.getNombre(),
                        p.getCapacity(),
                        p.getHabilitada()
                ))
                .toList();
    }

    public List<CinemaListDTO> findByHabilitada(boolean habilitada){
        return cinemaRepository.findByHabilitada(habilitada).stream()
                .map(c->new CinemaListDTO(
                        c.getId(),
                        c.getNombre(),
                        c.getCapacity(),
                        c.getHabilitada()
                ))
                .toList();
    }

    public List<CinemaListDTO> findByCapacidad(Integer capacidad){
        return cinemaRepository.findByCapacidadGreaterThan(0).stream()
                .map(c->new CinemaListDTO(
                        c.getId(),
                        c.getNombre(),
                        c.getCapacity(),
                        c.getHabilitada()
                ))
                .toList();
    }

    public CinemaDetailDTO updateById(Long id, CinemaRequestDTO entity){
        return cinemaRepository.findById(id).
                map(c -> {
                    c.setCapacity(entity.getCapacity());
                    Cinema created = cinemaRepository.save(c);
                    return new CinemaDetailDTO(
                            created.getId(),
                            created.getNombre(),
                            created.getTipoPantalla(),
                            created.getAtmos(),
                            created.getCapacity(),
                            created.getHabilitada()
                    );
                }).
                orElseThrow(() -> new NotFoundException("doesn't exist cinema ID: "+id));
    }

    public void deleteById (Long id){
        if (!cinemaRepository.existsById(id)){
            throw new NotFoundException("not found cinema ID: "+id);
        }
        cinemaRepository.deleteById(id);
    }

}
