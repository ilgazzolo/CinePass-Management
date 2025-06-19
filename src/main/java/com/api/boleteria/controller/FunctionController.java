package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.FunctionDetailDTO;
import com.api.boleteria.dto.list.FunctionListDTO;
import com.api.boleteria.dto.request.FunctionRequestDTO;
import com.api.boleteria.model.ScreenType;
import com.api.boleteria.service.FunctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de funciones (proyecciones de películas).
 *
 * Permite crear, consultar, actualizar y eliminar funciones, así como obtener
 * funciones disponibles por película o por tipo de pantalla.
 *
 * La mayoría de las operaciones requieren rol ADMIN, aunque la consulta está permitida
 * también para usuarios con rol CLIENT.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/functions")
public class FunctionController {
    private final FunctionService functionService;

    /**
     * Crea una nueva función.
     *
     * @param entity DTO con los datos necesarios para crear la función.
     * @return ResponseEntity con el detalle de la función creada.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunctionDetailDTO> create (@Valid @RequestBody FunctionRequestDTO entity){
        return ResponseEntity.ok(functionService.create(entity));
    }

    /**
     * Obtiene la lista de todas las funciones.
     *
     * @return ResponseEntity con una lista de funciones o 204 No Content si no hay funciones.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<FunctionListDTO>> getAll() {
        List<FunctionListDTO> list = functionService.findAll();
        if (list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene el detalle de una función específica por su ID.
     *
     * @param id Identificador de la función.
     * @return ResponseEntity con el detalle de la función.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<FunctionDetailDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(functionService.findById(id));
    }

    /**
     * Elimina una función por su ID.
     *
     * @param id Identificador de la función a eliminar.
     * @return ResponseEntity con estado 204 No Content si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        functionService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza una función por su ID.
     *
     * @param id Identificador de la función a actualizar.
     * @param entity DTO con los nuevos datos de la función.
     * @return ResponseEntity con el detalle actualizado de la función.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FunctionDetailDTO> update(@PathVariable Long id, @Valid @RequestBody FunctionRequestDTO entity){
        return ResponseEntity.ok(functionService.updateById(id, entity));
    }

    /**
     * Obtiene la lista de funciones disponibles para una película específica,
     * filtrando por capacidad disponible.
     *
     * @param movieId Identificador de la película.
     * @return ResponseEntity con la lista de funciones disponibles.
     */
    @GetMapping("/disponibles/{movieId}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<FunctionListDTO>> getAvailableFunctionsPerMovie(@PathVariable Long movieId) {
        List<FunctionListDTO> function = functionService.findByMovieIdAndAvailableCapacity(movieId);
        return ResponseEntity.ok(function);
    }

    /**
     * Obtiene la lista de funciones filtradas por tipo de pantalla.
     *
     * @param screenType Tipo de pantalla para filtrar las funciones.
     * @return ResponseEntity con la lista de funciones que coinciden con el tipo de pantalla.
     */
    @GetMapping("/tipo-pantalla/{screenType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<FunctionListDTO>> getByScreenType(@PathVariable ScreenType screenType) {
        List<FunctionListDTO> function = functionService.findByScreenType(screenType);
        return ResponseEntity.ok(function);
    }

}
