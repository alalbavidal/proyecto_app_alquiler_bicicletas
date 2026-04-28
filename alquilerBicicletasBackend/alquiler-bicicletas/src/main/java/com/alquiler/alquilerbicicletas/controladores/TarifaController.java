package com.alquiler.alquilerbicicletas.controladores;

import com.alquiler.alquilerbicicletas.enumerados.TipoTarifa;
import com.alquiler.alquilerbicicletas.modelos.Tarifa;
import com.alquiler.alquilerbicicletas.servicios.TarifaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/tarifas")
@RequiredArgsConstructor
public class TarifaController {

    private final TarifaService tarifaService;

    /**
     * Obtiene todas las tarifas.
     */
    @GetMapping
    public ResponseEntity<List<Tarifa>> listarTodas() {
        return ResponseEntity.ok(tarifaService.findAll());
    }

    /**
     * Valida una tarifa por su ID.
     */
    @GetMapping("/{id}/validar")
    public ResponseEntity<?> validarTarifa(@PathVariable Long id) {
        try {
            Tarifa tarifa = tarifaService.validarTarifa(id);
            return ResponseEntity.ok(tarifa);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }




}
