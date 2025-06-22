package com.api.boleteria.exception;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.nio.file.AccessDeniedException;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja de forma global las excepciones lanzadas en los controladores REST.
 *
 * Proporciona métodos específicos para manejar distintas excepciones y devolver
 * respuestas HTTP con el código y mensaje adecuado.
 */

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja excepciones lanzadas cuando el cuerpo de la solicitud no se puede leer o parsear correctamente.
     *
     * Esta excepción se lanza, por ejemplo, cuando el cliente envía un valor incompatible con el tipo esperado
     * (por ejemplo, un texto en lugar de un número).
     *
     * Si la causa es un {@link com.fasterxml.jackson.databind.exc.InvalidFormatException}, se extrae el campo específico
     * y el tipo esperado para construir un mensaje de error más claro.
     *
     * @param ex la excepción {@link HttpMessageNotReadableException} capturada.
     * @return ResponseEntity con un mensaje de error personalizado y estado HTTP 400 (Bad Request).
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        Throwable cause = ex.getCause();

        if (cause instanceof InvalidFormatException formatEx && !formatEx.getPath().isEmpty()) {
            // Arma el path completo por si está anidado (ej: pelicula.duracion)
            String fieldPath = formatEx.getPath().stream()
                    .map(ref -> ref.getFieldName())
                    .filter(name -> name != null)
                    .reduce((a, b) -> a + "." + b)
                    .orElse("campo desconocido");

            String expectedType = formatEx.getTargetType().getSimpleName();

            String message = "El campo '" + fieldPath + "' debe ser de tipo " + expectedType + ".";

            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(message);
        }

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Error en el cuerpo de la solicitud.");
    }

    /**
     * Maneja excepciones cuando un parámetro recibido en la URL (por ejemplo, vía @RequestParam o @PathVariable)
     * no puede convertirse al tipo esperado.
     *
     * Esta excepción ocurre comúnmente cuando se espera un número (como Long o Integer) y se recibe una cadena de texto,
     * o cualquier valor que no se pueda mapear correctamente.
     *
     * Ejemplo: si un endpoint espera `?id=5` pero se recibe `?id=abc`, se lanza esta excepción.
     *
     * @param ex la excepción {@link MethodArgumentTypeMismatchException} capturada.
     * @return ResponseEntity con un mensaje claro y estado HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<String> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        String nombreParametro = ex.getName();
        return ResponseEntity
                .badRequest()
                .body("El parámetro '" + nombreParametro + "' debe ser un número válido.");
    }



    /**
     * Maneja excepciones lanzadas cuando se violan restricciones de validación declaradas con anotaciones
     * como {@code @NotNull}, {@code @Min}, {@code @Max}, {@code @Pattern}, etc. en parámetros del controlador.
     *
     * Esta excepción típicamente ocurre en validaciones de parámetros de métodos del controlador cuando se
     * utiliza {@code @Validated} a nivel de clase o método.
     *
     * Ejemplo: si un parámetro anotado con {@code @Min(1)} recibe el valor 0, se lanza esta excepción.
     *
     * @param ex la excepción {@link ConstraintViolationException} capturada.
     * @return ResponseEntity con el mensaje del primer error y estado HTTP 400 (Bad Request).
     */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<String> handleConstraintViolationException(ConstraintViolationException ex) {
        String mensajeError = ex.getConstraintViolations().stream()
                .map(ConstraintViolation::getMessage)
                .findFirst()
                .orElse("Error de validación");

        return ResponseEntity.badRequest().body(mensajeError);
    }


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
     * Maneja excepciones lanzadas cuando fallan las validaciones de bean (@Valid) en los objetos del cuerpo
     * de la solicitud (por ejemplo, DTOs anotados con {@code @NotBlank}, {@code @Size}, etc.).
     *
     * Esta excepción se lanza automáticamente cuando se utiliza {@code @Valid} en parámetros del controlador
     * y alguna restricción de validación no se cumple.
     *
     * Este handler extrae y devuelve solo el primer mensaje de error detectado, simplificando la respuesta.
     *
     * @param ex Excepción {@link MethodArgumentNotValidException} capturada.
     * @return ResponseEntity con el primer mensaje de error y estado HTTP 400 (Bad Request).
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidationException(MethodArgumentNotValidException ex) {
        // Obtener solo el primer error para simplificar
        String mensajeError = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .findFirst()
                .map(err -> err.getDefaultMessage())
                .orElse("Error de validación");

        return ResponseEntity.badRequest().body(mensajeError);
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
