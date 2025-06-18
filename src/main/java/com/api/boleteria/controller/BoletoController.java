package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.BoletoDetailDTO;
import com.api.boleteria.dto.request.BoletoRequestDTO;
import com.api.boleteria.model.Boleto;
import com.api.boleteria.service.BoletoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/boletos")
@RequiredArgsConstructor
public class BoletoController {

    private final BoletoService boletoService;


    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/comprar")
    public ResponseEntity<BoletoDetailDTO> comprarBoleto(@RequestBody @Valid BoletoRequestDTO request) {
        BoletoDetailDTO boleto = boletoService.create(request);
        return ResponseEntity.ok(boleto);
    }


    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public ResponseEntity<List<BoletoDetailDTO>> getMisBoletos() {
        List<BoletoDetailDTO> boletos = boletoService.getBoletosDelUsuarioLogueado();
        return ResponseEntity.ok(boletos);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<BoletoDetailDTO> getBoletoById(@PathVariable Long id) {
        BoletoDetailDTO dto = boletoService.getBoletoById(id);
        return ResponseEntity.ok(dto);
    }

}
