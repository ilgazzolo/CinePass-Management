package com.api.boleteria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    ///  maneja la excepcion BadRequestException
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> BadRequestExceptionHandler(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }


    ///  Maneja la excepcion IllegalArgument
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> IllegalArgumentExceptionHandler(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }


    /// Maneja excepcion AccesDenied
    @ExceptionHandler(AccesDeniedException.class)
    public ResponseEntity<String> AccesDeniedExceptionsHandler(AccesDeniedException ex){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ex.getMessage());
    }


    /// Maneja excepcion UserNameNotFound
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> userNameNotFoundExceptionHandler(UsernameNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getLocalizedMessage());
    }


    ///  Maneja excepcion NotFound
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> NotFoundExceptionHandler(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    ///  Maneja excepciones generales
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> otherExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + ex.getMessage());
    }


}
