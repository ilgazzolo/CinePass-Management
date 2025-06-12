package com.api.boleteria.service;


import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.exception.NotFoundException;
import com.api.boleteria.model.Cinema;
import com.api.boleteria.model.Function;
import com.api.boleteria.repository.ICinemaRepository;
import com.api.boleteria.repository.IFunctionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CinemaService {

    private final ICinemaRepository cinemaRepository;

    private final IFunctionRepository functionRepository;

    public CinemaDetailDTO save(CinemaRequestDTO entity){
        Cinema cinema = new Cinema();
        cinema.setCapacity(entity.getCapacity());

        Cinema saved = cinemaRepository.save(cinema);

        return new CinemaDetailDTO(
                saved.getId(),
                saved.getCapacity(),
                saved.getFunctionList()
                        .stream()
                        .map(Function::getId)
                        .toList()
        );
    }

    public List<CinemaListDTO> findAll(){
        return cinemaRepository.findAll().stream().
                map(c -> new CinemaListDTO(
                        c.getId(),
                        c.getCapacity()
                ))
                .toList();
    }

    public CinemaDetailDTO findById(Long id){
        Cinema cinema = cinemaRepository.findById(id).
                orElseThrow(() -> new NotFoundException("doesn't exist movie ID: "+id));

        return new CinemaDetailDTO(
                cinema.getId(),
                cinema.getCapacity(),
                cinema.getFunctionList().stream()
                        .map(Function::getId)
                        .toList()
        );
    }

    public CinemaDetailDTO updateById(Long id, CinemaRequestDTO entity){
        return cinemaRepository.findById(id).
                map(c -> {
                    c.setCapacity(entity.getCapacity());
                    List<Function>functions = functionRepository.findAllById(entity.getFunctionsId());
                    c.setFunctionList(functions);

                    Cinema created = cinemaRepository.save(c);

                    return new CinemaDetailDTO(
                            created.getId(),
                            created.getCapacity(),
                            created.getFunctionList().stream()
                                    .map(Function::getId)
                                    .toList()
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
