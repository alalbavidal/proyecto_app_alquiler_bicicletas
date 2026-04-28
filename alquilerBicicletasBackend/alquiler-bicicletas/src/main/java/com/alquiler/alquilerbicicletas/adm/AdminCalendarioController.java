package com.alquiler.alquilerbicicletas.adm;

import com.alquiler.alquilerbicicletas.enumerados.EstadoBicicleta;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.alquiler.alquilerbicicletas.repositorios.BicicletaRepository;
import com.alquiler.alquilerbicicletas.repositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/admin/calendario")
@RequiredArgsConstructor
public class AdminCalendarioController {

    private final BicicletaRepository bicicletaRepository;
    private final ReservaRepository reservaRepository;

    @GetMapping("/dia")
    public ResponseEntity<AdminDiaResumenDTO> resumen(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        LocalDateTime ini = fecha.atStartOfDay();
        LocalDateTime fin = fecha.plusDays(1).atStartOfDay().minusSeconds(1);

        // total bicis activas
        long totalBicis = bicicletaRepository.findByEstado(EstadoBicicleta.ACTIVO).size();

        // reservas que pisan ese día
        List<Reserva> reservas = reservaRepository.findByRango(ini, fin);

        // bicis reservadas (distintas)
        long bicisReservadas = reservas.stream()
                .flatMap(r -> r.getBicicletas().stream())
                .map(rb -> rb.getBicicleta().getId())
                .distinct()
                .count();

        AdminDiaResumenDTO dto = new AdminDiaResumenDTO();
        dto.setFecha(fecha);
        dto.setTotalBicis((int) totalBicis);
        dto.setBicisReservadas((int) bicisReservadas);
        dto.setBicisLibres((int) (totalBicis - bicisReservadas));
        dto.setReservas(reservas.stream().map(AdminReservaListItemDTO::from).toList());

        return ResponseEntity.ok(dto);
    }
}

