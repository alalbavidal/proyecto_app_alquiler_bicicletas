// src/main/java/com/alquiler/alquilerbicicletas/adm/AdminReservasController.java
package com.alquiler.alquilerbicicletas.adm;

import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/adm/reservas")
@RequiredArgsConstructor
public class AdminReservaController {
    private final AdminReservaService service;

    @GetMapping
    public Page<ReservaAdminDTO> listar(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String estado,
            @RequestParam(required = false) Boolean pagado,
            @RequestParam(required = false) String fechaInicio,
            @RequestParam(required = false) String fechaFin,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return service.listarConFiltros(q, estado, pagado, fechaInicio, fechaFin, page, size);
    }




    @PutMapping("/{id}/cancelar")
    public ResponseEntity<?> cancelar(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.cancelarPorId(id));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje","Reserva no encontrada"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            service.eliminarPorId(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje","Reserva no encontrada"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }


    @PutMapping("/{id}/pagar")
    public ResponseEntity<?> pagar(@PathVariable Long id) {
        try {
            var dto = service.pagadorPorId(id);
            return ResponseEntity.ok(dto); // 👈 devolvemos la reserva actualizada
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(404).body(Map.of("mensaje", "Reserva no encontrada"));
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    @PutMapping("/{id}/tipo-cobro")
    public ResponseEntity<?> actualizarTipoCobro(
            @PathVariable Long id,
            @RequestParam(required = false) String tipoCobro
    ) {
        try {
            // Normalizar string a mayúsculas si no es null
            var dto = service.tipoCobroPorId(id, tipoCobro != null ? tipoCobro.toUpperCase() : null);
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }

    public record ObservacionesDTO(String observaciones) {}

    @PutMapping("/{id}/observaciones")
    public ResponseEntity<?> actualizarObservaciones(
            @PathVariable Long id,
            @RequestBody ObservacionesDTO body
    ) {
        try {
            var dto = service.actualizarObservaciones(id, body.observaciones());
            return ResponseEntity.ok(dto);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", e.getMessage()));
        }
    }








}
