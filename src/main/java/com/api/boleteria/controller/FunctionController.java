package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.FunctionDetailDTO;
import com.api.boleteria.dto.list.FunctionListDTO;
import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.service.FunctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/funciones")
public class FunctionController {
    private final FunctionService functionService;

    @PostMapping("/register")
    public ResponseEntity<FunctionDetailDTO> create (@Valid @RequestBody FunctionRequestDTO entity){
        return ResponseEntity.ok(functionService.create(entity));
    }

    @GetMapping
    public ResponseEntity<List<FunctionListDTO>> getAll(){
        List<FunctionListDTO> list = functionService.findAll();
        if (list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<FunctionDetailDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(functionService.findById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        functionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<FunctionDetailDTO> update(@PathVariable Long id, @Valid @RequestBody FunctionRequestDTO entity){
        return ResponseEntity.ok(functionService.updateById(id, entity));
    }

    @GetMapping("/disponibles/{movieId}")
    public ResponseEntity<List<FunctionListDTO>> getFuncionesDisponiblesPorPelicula(@PathVariable Long movieId) {
        List<FunctionListDTO> funciones = functionService.findAvailableByMovieId(movieId);
        return ResponseEntity.ok(funciones);
    }


}
