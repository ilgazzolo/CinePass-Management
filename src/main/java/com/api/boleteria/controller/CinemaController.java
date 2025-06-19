package com.api.boleteria.controller;

import com.api.boleteria.dto.detail.CinemaDetailDTO;
import com.api.boleteria.dto.list.CinemaListDTO;
import com.api.boleteria.dto.request.CinemaRequestDTO;
import com.api.boleteria.model.ScreenType;
import com.api.boleteria.service.CinemaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para la gestión de cines.
 *
 * Permite realizar operaciones CRUD sobre los cines, así como consultas
 * filtradas por tipo de pantalla, estado habilitado y capacidad de asientos.
 *
 * La mayoría de las operaciones están restringidas a usuarios con rol ADMIN.
 */

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cinemas")
public class CinemaController {

    @Autowired
    private CinemaService cinemaService;

    /**
     * Crea un nuevo cine.
     *
     * @param entity DTO con la información del cine a crear.
     * @return ResponseEntity con el detalle del cine creado.
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CinemaDetailDTO> create(@Valid @RequestBody CinemaRequestDTO entity){
        return ResponseEntity.ok(cinemaService.save(entity));
    }

    /**
     * Obtiene la lista de todos los cines.
     *
     * @return ResponseEntity con una lista de DTOs de cines o un 204 No Content si no hay cines.
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<CinemaListDTO>> getList(){
        List<CinemaListDTO> list = cinemaService.findAll();

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene el detalle de un cine específico por su ID.
     *
     * @param id Identificador del cine.
     * @return ResponseEntity con el detalle del cine solicitado.
     */

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<CinemaDetailDTO> getById(@PathVariable Long id){
        return ResponseEntity.ok(cinemaService.findById(id));
    }

    /**
     * Obtiene la lista de cines filtrados por tipo de pantalla.
     *
     * @param screenType Tipo de pantalla para filtrar.
     * @return ResponseEntity con la lista de cines o 204 No Content si no hay coincidencias.
     */

    @GetMapping("/ScreenType/{screenType}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<CinemaListDTO>> getByScreenType(@PathVariable ScreenType screenType){
        List<CinemaListDTO> list = cinemaService.findByScreenType(screenType);

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene la lista de cines filtrados por estado habilitado de la sala.
     *
     * @param enabled Estado habilitado para filtrar (true o false).
     * @return ResponseEntity con la lista de cines o 204 No Content si no hay coincidencias.
     */

    @GetMapping("/Enabled/{enabled}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('CLIENT')")
    public ResponseEntity<List<CinemaListDTO>> getByEnabledRoom(@PathVariable boolean enabled){
        List<CinemaListDTO> list = cinemaService.findByEnabledRoom(enabled);

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Obtiene la lista de cines filtrados por capacidad de asientos.
     *
     * @param capacity Capacidad de asientos para filtrar.
     * @return ResponseEntity con la lista de cines o 204 No Content si no hay coincidencias.
     */

    @GetMapping("/Capacity/{capacity}")
    public ResponseEntity<List<CinemaListDTO>> getBySeatCapacity(@PathVariable Integer capacity){
        List<CinemaListDTO> list = cinemaService.findBySeatCapacity(capacity);

        if(list.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(list);
    }

    /**
     * Elimina un cine por su ID.
     *
     * @param id Identificador del cine a eliminar.
     * @return ResponseEntity con estado 204 No Content si la eliminación fue exitosa.
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        cinemaService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Actualiza la información de un cine por su ID.
     *
     * @param id Identificador del cine a actualizar.
     * @param entity DTO con la nueva información para el cine.
     * @return ResponseEntity con el detalle actualizado del cine.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<CinemaDetailDTO> update(@PathVariable Long id, @Valid @RequestBody CinemaRequestDTO entity){
        return ResponseEntity.ok(cinemaService.updateById(id, entity));
    }
}
