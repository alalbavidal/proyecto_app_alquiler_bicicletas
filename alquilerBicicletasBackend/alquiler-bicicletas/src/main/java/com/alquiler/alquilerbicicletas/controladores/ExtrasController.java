package com.alquiler.alquilerbicicletas.controladores;

import com.alquiler.alquilerbicicletas.dto.ExtraDTO;
import com.alquiler.alquilerbicicletas.dto.ReservaExtraLineDTO;
import com.alquiler.alquilerbicicletas.dto.TotalesExtrasDTO;
import com.alquiler.alquilerbicicletas.enumerados.AplicableExtra;
import com.alquiler.alquilerbicicletas.modelos.Extra;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.alquiler.alquilerbicicletas.modelos.ReservaExtra;
import com.alquiler.alquilerbicicletas.repositorios.ExtraRepository;
import com.alquiler.alquilerbicicletas.servicios.ExtrasService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/extras")
@RequiredArgsConstructor
public class ExtrasController {

    private final ExtrasService extrasService;
    private final ExtraRepository extraRepository;

    // =========================
    // DTOs de request/response
    // =========================

    @Data
    public static class AgregarExtraRequest {
        private Long extraId;
        private int cantidad;
        private AplicableExtra contexto; // RESERVA o DEVOLUCION (o AMBOS si lo fuerzas)
    }

    @Data
    public static class TotalesReservaResponse {
        private Long reservaId;
        private String estado;
        private String mensaje;
        private java.math.BigDecimal precioTotal;
        private java.math.BigDecimal extrasTotal;

        public TotalesReservaResponse(Reserva r, String mensaje) {
            this.reservaId = r.getId();
            this.estado = r.getEstado().name();
            this.precioTotal = r.getPrecioTotal();
            this.extrasTotal = r.getExtrasTotal();
            this.mensaje = mensaje;
        }
    }


    // =========================
    // Endpoints básicos
    // =========================

    /**
     * Agregar (o acumular) un extra a una reserva confirmada.
     * Body: { "extraId": 4, "cantidad": 2, "contexto": "RESERVA" }
     */
    @PostMapping("/reserva/{reservaId}")
    public ResponseEntity<?> agregarExtra(
            @PathVariable Long reservaId,
            @RequestBody AgregarExtraRequest req
    ) {
        try {
            Reserva r = extrasService.agregarExtra(reservaId, req.getExtraId(), req.getCantidad(), req.getContexto());
            return ResponseEntity.ok(new TotalesExtrasDTO(r));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }




    /**
     * Eliminar la línea completa del extra en la reserva.
     */
    @DeleteMapping("/reserva/{reservaId}/{extraId}")
    public ResponseEntity<?> eliminarExtra(
            @PathVariable Long reservaId,
            @PathVariable Long extraId
    ) {
        try {
            Reserva r = extrasService.eliminarExtra(reservaId, extraId);
            return ResponseEntity.ok(new TotalesReservaResponse(r, "Extra eliminado correctamente."));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Listar extras, opcionalmente filtrando por aplicabilidad.
     * /api/extras            → todos
     * /api/extras?aplicable=RESERVA|DEVOLUCION|AMBOS
     */
    @GetMapping
    public ResponseEntity<List<ExtraDTO>> listarExtras(
            @RequestParam(name = "aplicable", required = false) AplicableExtra aplicable
    ) {
        // forzamos que solo devuelva RESERVA
        List<Extra> extras = extrasService.listar(AplicableExtra.RESERVA);
        List<ExtraDTO> dtoList = extras.stream()
                .map(e -> new ExtraDTO(
                        e.getId(),
                        e.getNombre(),
                        e.getDescripcion(),
                        e.getPrecio(),
                        e.getAplicableA(),
                        e.getRestricciones()
                ))
                .toList();
        return ResponseEntity.ok(dtoList);
    }




    /**
     * Listar las líneas de extras de una reserva.
     */
    @GetMapping("/reserva/{reservaId}")
    public ResponseEntity<List<ReservaExtraLineDTO>> listarExtrasDeReserva(@PathVariable Long reservaId) {
        List<ReservaExtra> lineas = extrasService.listarDeReserva(reservaId);
        List<ReservaExtraLineDTO> dto = lineas.stream()
                .map(ReservaExtraLineDTO::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(dto);
    }

    /**
     * Obtener un extra por su id.
     */
    @GetMapping("/{extraId}")
    public ResponseEntity<?> obtenerExtra(@PathVariable Long extraId) {
        return extraRepository.findById(extraId)
                .<ResponseEntity<?>>map(e -> ResponseEntity.ok(
                        new ExtraDTO(e.getId(), e.getNombre(), e.getDescripcion(), e.getPrecio())
                ))
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
