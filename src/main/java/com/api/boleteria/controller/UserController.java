package com.api.boleteria.controller;

import com.api.boleteria.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@AllArgsConstructor
@RequestMapping("/api/GestionUsuarios")
public class UserController {
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



    //ver pefil
    //
}
