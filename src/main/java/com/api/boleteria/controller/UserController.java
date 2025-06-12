package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.MovieDetailDTO;
import com.api.boleteria.dto.detail.UserDetailDTO;
import com.api.boleteria.dto.list.MovieListDTO;
import com.api.boleteria.dto.request.MovieRequestDTO;
import com.api.boleteria.dto.request.UserRequestDTO;
import com.api.boleteria.model.User;
import com.api.boleteria.service.UserService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/auth")
@AllArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<UserDetailDTO> register (@Valid @RequestBody UserRequestDTO req){
        return ResponseEntity.ok(userService.save(req));
    }



}
