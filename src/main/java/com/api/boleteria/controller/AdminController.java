package com.api.boleteria.controller;

import com.api.boleteria.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;


    @PutMapping("/{username}/make-admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> makeUserAdmin(@PathVariable String username) {
        boolean updated = userService.makeUserAdmin(username);
        if (updated) {
            return ResponseEntity.ok("Usuario " + username + " ahora es ADMIN");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Usuario no encontrado");
        }
    }




}
