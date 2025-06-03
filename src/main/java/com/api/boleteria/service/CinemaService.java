package com.api.boleteria.service;


import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.repository.ICinemaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CinemaService {

    @Autowired
    private ICinemaRepository cinemaRepository;

    public CinemaDetailDTO save(CinemaRequestDTO entity){
        Cinema cinema = new Cinema();
        cinema.setCapacity(entity.getCapacity());

        Cinema saved = cinemaRepository.save(cinema);

        return new CinemaDetailDTO(
                saved.getId(),
                saved.getCapacity(),
                saved.getFunctionList()
        );
    }

    public List<CinemaListDTO> findAll(){
        return cinemaRepository.findAll().stream().
                map(c -> new CinemaListDTO(
                        c.getId(),
                        c.getCapacity(),
                        c.getFunctionList()
                ))
                .toList();
    }

    public CinemaDetailDTO findById(Long id){
        Cinema cinema = cinemaRepository.findById(id).
                orElseThrow(() -> new RuntimeException("crear una excepcion " + id));

        return new CinemaDetailDTO(
                cinema.getId(),
                cinema.getCapacity(),
                cinema.getFunctionList()
        );
    }

    public CinemaDetailDTO updateById(Long id, CinemaRequestDTO entity){
        return cinemaRepository.findById(id).
                map(c -> {
                    c.setCapacity(entity.getCapacity());
                    c.setFunctionList(entity.getFunctions());

                    Cinema created = cinemaRepository.save(c);

                    return new CinemaDetailDTO(
                            created.getId(),
                            created.getCapacity(),
                            created.getFunctionList()
                    );
                }).
                orElseThrow(() -> new RuntimeException("crear una excepcion " + id));
    }

    public void deleteById (Long id){
        if (!cinemaRepository.existsById(id)){
            throw new NotFoundException("not found ID function: "+id);
        }
        cinemaRepository.deleteById(id);
    }

}
