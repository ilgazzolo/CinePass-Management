package com.api.boleteria.controller;

import com.api.boleteria.dto.FunctionDetailDTO;
import com.api.boleteria.dto.FunctionRequestDTO;
import com.api.boleteria.service.FunctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/funciones")
public class FunctionController {
    private final FunctionService functionService;

    //POST, GET, DELETE, PUT
    @PostMapping
    public ResponseEntity<FunctionDetailDTO> create (@Valid @RequestBody FunctionRequestDTO entity){
        return ResponseEntity.ok(functionService.create(entity));
    }

}
