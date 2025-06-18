package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.model.ScreenType;
import com.api.boleteria.service.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cinemas")

public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CinemaDetailDTO> create(@Valid @RequestBody CinemaRequestDTO entity){
        return ResponseEntity.ok(cinemaService.save(entity));
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<CinemaListDTO>> getList(){
        List<CinemaListDTO> lista = cinemaService.findAll();

        if(lista.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CinemaDetailDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(cinemaService.findById(id));
    }

    @GetMapping("/TipoPantalla/{tipoPantalla}")
    public ResponseEntity<List<CinemaListDTO>> getByTipoPantalla(@PathVariable ScreenType screenType){
        List<CinemaListDTO> lista = cinemaService.findByScreenType(screenType);

        if(lista.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/Habilitada/{habilitada}")
    public ResponseEntity<List<CinemaListDTO>> getByHabilitada(@PathVariable boolean habilitada){
        List<CinemaListDTO> lista = cinemaService.findByEnabledRoom(habilitada);

        if(lista.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/Capacidad/{capacidad}")
    public ResponseEntity<List<CinemaListDTO>> getByCapacidad(@PathVariable Integer capacidad){
        List<CinemaListDTO> lista = cinemaService.findBySeatCapacity(capacidad);

        if(lista.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(lista);
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        cinemaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CinemaDetailDTO> update(@PathVariable Long id, @Valid @RequestBody CinemaRequestDTO entity){
        return ResponseEntity.ok(cinemaService.updateById(id, entity));
    }
}
