package com.api.boleteria.controller;

import com.api.boleteria.model.Boleto;
import com.api.boleteria.service.BoletoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/client")
@AllArgsConstructor
@PreAuthorize("hasRole('CLIENT')")  // Solo usuarios con rol CLIENT pueden acceder
public class ClientController {

    private final BoletoService boletoService;

    // Endpoint para que el cliente vea sus boletos comprados
    @GetMapping("/boletos")
    public ResponseEntity<List<Boleto>> getMisBoletos() {
        List<Boleto> boletos = boletoService.getBoletosDelUsuarioLogueado();
        return ResponseEntity.ok(boletos);
    }

    // Endpoint para comprar boleto (simplificado)
    @PostMapping("/boletos/comprar")
    public ResponseEntity<String> comprarBoleto(@RequestParam Long funcionId) {
        boolean exito = boletoService.comprarBoleto(funcionId);
        if (exito) {
            return ResponseEntity.ok("Boleto comprado con éxito");
        } else {
            return ResponseEntity.badRequest().body("No se pudo comprar el boleto");
        }
    }

    // Puedes agregar más endpoints específicos para cliente aquí...
}

