package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.UserListDTO;
import com.api.boleteria.dto.request.RegisterRequestDTO;
import com.api.boleteria.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de usuarios.
 *
 * Permite obtener, actualizar usuarios y gestionar roles.
 * Algunos endpoints requieren que el usuario tenga rol ADMIN,
 * mientras que otros permiten acceso a CLIENT o ADMIN.
 */
@RestController
@AllArgsConstructor
@RequestMapping("/api/userManagement")
public class UserController {
    private final UserService userService;

    /**
     * Otorga rol ADMIN a un usuario existente identificado por su username.
     *
     * @param username Nombre de usuario a actualizar.
     * @return ResponseEntity con mensaje de éxito o error si el usuario no existe.
     */
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

    /**
     * Obtiene la lista de todos los usuarios.
     *
     * @return ResponseEntity con la lista de usuarios.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserListDTO>> getAllUsers() {
        List<UserListDTO> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Obtiene el detalle de un usuario por su username.
     *
     * @param username Nombre de usuario.
     * @return ResponseEntity con el detalle del usuario.
     */
    @GetMapping("/Username/{username}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailDTO> getUserByUsername(@PathVariable String username) {
        UserDetailDTO dto = userService.findByUsername(username);
        return ResponseEntity.ok(dto);
    }

    /**
     * Actualiza la información del usuario autenticado (o de un usuario, según contexto).
     *
     * @param entity DTO con la información para actualizar.
     * @return ResponseEntity con el usuario actualizado.
     */

    @PutMapping("/me")
    @PreAuthorize("hasRole('ADMIN')or hasRole('CLIENT')")
    public ResponseEntity<UserDetailDTO> update(@RequestBody RegisterRequestDTO entity) {
        UserDetailDTO updated = userService.update(entity);
        return ResponseEntity.ok(updated);
    }

    /**
     * Obtiene el detalle de un usuario por su ID.
     *
     * @param id Identificador del usuario.
     * @return ResponseEntity con el detalle del usuario.
     */

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDetailDTO> findById(@PathVariable Long id) {
        UserDetailDTO user = userService.findById(id);
        return ResponseEntity.ok(user);
    }

    /**
     * Obtiene el perfil del usuario autenticado.
     *
     * @return ResponseEntity con el detalle del usuario autenticado.
     */

    @GetMapping("/me")
    @PreAuthorize("hasRole('ADMIN')or hasRole('CLIENT')")
    public ResponseEntity<UserDetailDTO> getMyProfile() {
        UserDetailDTO user = userService.getProfile();
        return ResponseEntity.ok(user);
    }

}
