package com.api.boleteria.controller;

import com.api.boleteria.config.JwtUtil;
import com.api.boleteria.dto.request.LoginRequestDTO;
import com.api.boleteria.dto.request.RegisterRequestDTO;
import com.api.boleteria.repository.IUserRepository;
import com.api.boleteria.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import com.api.boleteria.model.User;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class AuthController {


    private final AuthenticationManager authManager; // Gestiona la autenticación
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
