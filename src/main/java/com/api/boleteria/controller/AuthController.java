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
    private final IUserRepository userRepo;

    @PostMapping("/login")              // Endpoint POST /api/auth/login
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginRequestDTO req) {
        // 1) Construir token de autenticación
        UsernamePasswordAuthenticationToken upToken =
                new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword());

        // 2) Autenticar credenciales
        Authentication auth = authManager.authenticate(upToken);

        // 3) Generar JWT si éxito
        UserDetails user = (UserDetails) auth.getPrincipal();
        String jwt = JwtUtil.createToken(user.getUsername(),
                user.getAuthorities().stream()
                        .map(a -> a.getAuthority().replace("ROLE_", ""))
                        .collect(Collectors.toList()));

        // 4) Devolver token
        return ResponseEntity.ok(Map.of("token", jwt));
    }


    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody RegisterRequestDTO req) {
        // Validar si username o email ya existen
        if (userRepo.existsByUsername(req.getUsername())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Usuario o email ya en uso");
        }

        System.out.println("Password recibido: " + req.getPassword());

        // Crear y guardar nuevo usuario (encriptar password)
        User nuevo = new User(
                req.getName(),
                req.getSurname(),
                req.getUsername(),
                req.getEmail(),
                new BCryptPasswordEncoder().encode(req.getPassword()) // encriptar
        );

        userRepo.save(nuevo);

        return ResponseEntity.status(HttpStatus.CREATED).body("Usuario registrado con éxito");
    }


}

