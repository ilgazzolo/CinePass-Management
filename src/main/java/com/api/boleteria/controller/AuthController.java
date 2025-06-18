package com.api.boleteria.controller;

import com.api.boleteria.dto.request.LoginRequestDTO;
import com.api.boleteria.dto.request.RegisterRequestDTO;
import com.api.boleteria.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {


    private final AuthenticationManager authManager;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO req) {
        return ResponseEntity.ok(userService.login(req, authManager));
    }



    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO req) {
        // Validar si username o email ya existen
        if (userService.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Username ya en uso");
        }

        userService.save(req);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito");
    }

}
