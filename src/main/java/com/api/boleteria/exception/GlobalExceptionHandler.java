package com.api.boleteria.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.file.AccessDeniedException;

/**
 * Maneja de forma global las excepciones lanzadas en los controladores REST.
 *
 * Proporciona métodos específicos para manejar distintas excepciones y devolver
 * respuestas HTTP con el código y mensaje adecuado.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja la excepción BadRequestException y devuelve una respuesta con estado 400.
     *
     * @param ex Excepción BadRequestException capturada.
     * @return ResponseEntity con mensaje de error y estado HTTP 400 (Bad Request).
     */

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> BadRequestExceptionHandler(BadRequestException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }


    /**
     * Maneja la excepción IllegalArgumentException y devuelve una respuesta con estado 400.
     *
     * @param ex Excepción IllegalArgumentException capturada.
     * @return ResponseEntity con mensaje de error y estado HTTP 400 (Bad Request).
     */

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> IllegalArgumentExceptionHandler(IllegalArgumentException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }


    /**
     * Maneja la excepción AccesDeniedException y devuelve una respuesta con estado 401.
     *
     * @param ex Excepción AccesDeniedException capturada.
     * @return ResponseEntity con mensaje de error y estado HTTP 403 (Forbidden).
     */

    @ExceptionHandler(AccesDeniedException.class)
    public ResponseEntity<String> AccesDeniedExceptionsHandler(AccesDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }

    /**
     * Maneja excepciones de acceso denegado lanzadas cuando un usuario no tiene permisos suficientes.
     *
     * Este handler se activa cuando se lanza una {@link AccessDeniedException}, por ejemplo,
     * al intentar acceder a un recurso protegido sin el rol correspondiente.
     *
     * @param ex Excepción que contiene el mensaje de error.
     * @return ResponseEntity con el mensaje de error y el estado HTTP 403 (Forbidden).
     */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<String> AccesDeniedExceptionsDefaultHandler(AccessDeniedException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
    }



    /**
     * Maneja la excepción UsernameNotFoundException y devuelve una respuesta con estado 404.
     *
     * @param ex Excepción UsernameNotFoundException capturada.
     * @return ResponseEntity con mensaje de error y estado HTTP 404 (Not Found).
     */

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<String> userNameNotFoundExceptionHandler(UsernameNotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getLocalizedMessage());
    }


    /**
     * Maneja la excepción NotFoundException y devuelve una respuesta con estado 404.
     *
     * @param ex Excepción NotFoundException capturada.
     * @return ResponseEntity con mensaje de error y estado HTTP 404 (Not Found).
     */

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> NotFoundExceptionHandler(NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    /**
     * Maneja excepciones generales no capturadas específicamente.
     *
     * @param ex Excepción general capturada.
     * @return ResponseEntity con mensaje de error y estado HTTP 500 (Internal Server Error).
     */

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> otherExceptionHandler(Exception ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error inesperado: " + ex.getMessage());
    }


}
