package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.BoletoDetailDTO;
import com.api.boleteria.dto.request.BoletoRequestDTO;
import com.api.boleteria.service.BoletoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/boletos")
@RequiredArgsConstructor
public class BoletoController {

    private final BoletoService boletoService;

    @PostMapping("/comprar")
    public ResponseEntity<BoletoDetailDTO> comprarBoleto(@RequestBody @Valid BoletoRequestDTO request) {
        BoletoDetailDTO boleto = boletoService.create(request);
        return ResponseEntity.ok(boleto);
    }
}
