package com.alquiler.alquilerbicicletas.controladores;

import com.alquiler.alquilerbicicletas.dto.AccesorioDTO;
import com.alquiler.alquilerbicicletas.dto.AccesorioDisponibilidadResponseDTO;
import com.alquiler.alquilerbicicletas.dto.AccesorioOpcionDTO;
import com.alquiler.alquilerbicicletas.dto.DisponibilidadAccesorioRequestDTO;
import com.alquiler.alquilerbicicletas.servicios.AccesorioService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/accesorios")
@RequiredArgsConstructor
public class AccesorioController {

    private final AccesorioService accesorioService;

    @GetMapping
    public ResponseEntity<List<AccesorioDTO>> listar() {
        return ResponseEntity.ok(accesorioService.listarAccesoriosActivos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AccesorioDTO> obtenerPorId(@PathVariable Long id) {
        return accesorioService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<AccesorioDTO>> filtrar(
            @RequestParam(required = false) String nombre,
            @RequestParam(required = false) Boolean activo,
            @RequestParam(required = false) Boolean disponibleStock // true = stock > 0
    ) {
        List<AccesorioDTO> resultados = accesorioService.filtrar(nombre, activo, disponibleStock);
        return ResponseEntity.ok(resultados);
    }


    @PostMapping
    public ResponseEntity<AccesorioDTO> crear(@RequestBody AccesorioDTO dto) {
        return ResponseEntity.ok(accesorioService.crear(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AccesorioDTO> actualizar(@PathVariable Long id, @RequestBody AccesorioDTO dto) {
        return accesorioService.actualizar(id, dto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        boolean eliminado = accesorioService.eliminarLogicamente(id);
        return eliminado ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @PostMapping("/disponibilidad")
    public ResponseEntity<AccesorioDisponibilidadResponseDTO> verificarDisponibilidad(
            @RequestBody DisponibilidadAccesorioRequestDTO request
    ) {
        return ResponseEntity.ok(accesorioService.verificarDisponibilidadAccesorios(request));
    }

    // AccesorioController.java
    @GetMapping("/opciones")
    public ResponseEntity<List<AccesorioOpcionDTO>> opciones(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin,
            @RequestParam Integer numeroBicis
    ) {
        var dtos = accesorioService.calcularOpcionesAccesorios(fechaInicio, fechaFin, numeroBicis);
        return ResponseEntity.ok(dtos);
    }







}
