// src/main/java/com/alquiler/alquilerbicicletas/controladores/AdminTarifaController.java
package com.alquiler.alquilerbicicletas.adm;

import com.alquiler.alquilerbicicletas.dto.TarifaDTO;
import com.alquiler.alquilerbicicletas.modelos.Tarifa;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/tarifas")
@RequiredArgsConstructor
public class AdminTarifaController {

    private final TarifaAdminService service;

    @GetMapping
    public ResponseEntity<List<TarifaDTO>> listar() {
        var list = service.listar().stream().map(this::toDTO).toList();
        return ResponseEntity.ok(list);
    }

    @GetMapping("/{id}")
    public ResponseEntity<TarifaDTO> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(toDTO(service.obtener(id)));
    }

    @PostMapping
    public ResponseEntity<TarifaDTO> crear(@RequestBody TarifaDTO dto) {
        var saved = service.crearOActualizar(dto, null);
        return ResponseEntity.ok(toDTO(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TarifaDTO> actualizar(@PathVariable Long id, @RequestBody TarifaDTO dto) {
        var saved = service.crearOActualizar(dto, id);
        return ResponseEntity.ok(toDTO(saved));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/activa")
    public ResponseEntity<TarifaDTO> setActiva(@PathVariable Long id, @RequestParam boolean value) {
        var saved = service.setActiva(id, value);
        return ResponseEntity.ok(toDTO(saved));
    }

    private TarifaDTO toDTO(Tarifa t) {
        return TarifaDTO.builder()
                .id(t.getId())
                .nombre(t.getNombre())
                .descripcion(t.getDescripcion())
                .precio(t.getPrecio())
                .horasIncluidas(t.getHorasIncluidas())
                .diasMinimos(t.getDiasMinimos())
                .precioPorDia(t.isPrecioPorDia())
                .activa(t.isActiva())
                .horaInicio(t.getHoraInicio())
                .horaFin(t.getHoraFin())
                .build();
    }
}
