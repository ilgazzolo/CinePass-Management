package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.TicketDetailDTO;
import com.api.boleteria.dto.request.TicketRequestDTO;
import com.api.boleteria.service.TicketService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;


    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/comprar")
    public ResponseEntity<List<TicketDetailDTO>> buyTicket(@RequestBody @Valid TicketRequestDTO request) {
        List<TicketDetailDTO> listTickets = ticketService.buyTickets(request);
        return ResponseEntity.ok(listTickets);
    }


    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping
    public ResponseEntity<List<TicketDetailDTO>> getTickets() {
        List<TicketDetailDTO> boletos = ticketService.findTicketsFromAuthenticatedUser();
        return ResponseEntity.ok(boletos);
    }

    @PreAuthorize("hasRole('CLIENT')")
    @GetMapping("/{id}")
    public ResponseEntity<TicketDetailDTO> getTicketById(@PathVariable Long id) {
        TicketDetailDTO dto = ticketService.findTicketById(id);
        return ResponseEntity.ok(dto);
    }

}
