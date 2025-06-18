package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.UserListDTO;
import com.api.boleteria.dto.request.RegisterRequestDTO;
import com.api.boleteria.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        List<UserListDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/Username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailDTO> getUserByUsername(@PathVariable String username) {
        UserDetailDTO dto = userService.findByUsername(username);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/me")
    @PreAuthorize("hasRole('ADMIN')or hasRole('CLIENT')")
    public ResponseEntity<UserDetailDTO> update(@RequestBody RegisterRequestDTO dto) {
        UserDetailDTO updated = userService.update(dto);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailDTO> findById(@PathVariable Long id) {
        UserDetailDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')or hasRole('CLIENT')")
    public ResponseEntity<UserDetailDTO> verMiPerfil() {
        UserDetailDTO user = userService.getProfile();
        return ResponseEntity.ok(user);
    }

}
