// src/main/java/com/alquiler/alquilerbicicletas/adm/AdminReservaService.java
package com.alquiler.alquilerbicicletas.adm;

import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.enumerados.TipoCobro;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.alquiler.alquilerbicicletas.repositorios.ReservaRepository;
import com.alquiler.alquilerbicicletas.servicios.CancelarReservaService;
import com.alquiler.alquilerbicicletas.util.ReservaSpecs;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminReservaService {

    private final ReservaRepository reservaRepository;
    private final CancelarReservaService cancelarReservaService;

    public List<ReservaAdminDTO> listar() {
        return reservaRepository.findAllByOrderByFechaInicioDesc()
                .stream()
                .map(this::toDTO)
                .toList();
    }



    public Page<ReservaAdminDTO> listarConFiltros(
            String q,
            String estadoStr,
            Boolean pagado,
            String fechaInicioStr,
            String fechaFinStr,
            int page,
            int size
    ) {
        EstadoReserva estado = null;
        if (estadoStr != null && !estadoStr.isBlank()) {
            estado = EstadoReserva.valueOf(estadoStr);
        }

        LocalDateTime fechaInicioFiltro = null;
        LocalDateTime fechaFinFiltro = null;
        try {
            if (fechaInicioStr != null && !fechaInicioStr.isBlank()) {
                fechaInicioFiltro = LocalDateTime.parse(fechaInicioStr);
            }
            if (fechaFinStr != null && !fechaFinStr.isBlank()) {
                fechaFinFiltro = LocalDateTime.parse(fechaFinStr);
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato de fecha inválido, debe ser ISO-8601");
        }

        // Siempre considerar solo reservas futuras
        LocalDateTime ahora = LocalDateTime.now();

        Specification<Reserva> spec = Specification
                .where(ReservaSpecs.conClienteLike(q))
                .and(ReservaSpecs.conEstado(estado))
                .and(ReservaSpecs.conPagado(pagado))
                // Reservas futuras + filtros de fechas opcionales
                .and(ReservaSpecs.conFechaInicioFin(
                        fechaInicioFiltro != null ? fechaInicioFiltro : ahora,
                        fechaFinFiltro
                ));

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "fechaInicio"));

        Page<Reserva> reservasPage = reservaRepository.findAll(spec, pageable);

        return reservasPage.map(this::toDTO);
    }

    private ReservaAdminDTO toDTO(Reserva r) {
        var cliente = r.getCliente();
        var nombre = (cliente != null ? cliente.getNombre() : null);
        var apellido = (cliente != null ? cliente.getApellido() : null);
        var email = (cliente != null ? cliente.getEmail() : null);
        var telefono = (cliente != null ? cliente.getTelefono() : null);

        return new ReservaAdminDTO(
                r.getId(),
                nombre,
                apellido,
                email,
                telefono,
                r.getFechaInicio(),
                r.getFechaFin(),
                r.getPrecioTotal(),
                r.getExtrasTotal() != null ? r.getExtrasTotal() : BigDecimal.ZERO,
                r.getEstado(),
                r.isPagado(),
                r.getContratoUrl(),
                r.getEstado(),
                r.getTipoCobro(),
                r.getObservaciones()
        );
    }




    public ReservaAdminDTO cancelarPorId(Long id) {
        cancelarReservaService.cancelarPorId(id); // reutiliza tu lógica de cancelación
        // recargamos para devolver el estado actualizado
        var r = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada tras cancelar"));
        return toDTO(r);
    }

   public void eliminarPorId(Long id) {
       var r = reservaRepository.findById(id)
               .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
       if (!"CANCELADA".equalsIgnoreCase(r.getEstado().name())) {
           throw new IllegalStateException("Solo se pueden eliminar reservas canceladas");
       }
       reservaRepository.deleteById(id);
   }








    public ReservaAdminDTO pagadorPorId(Long id) {
        var r = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        r.setPagado(!r.isPagado()); // Alterna el estado
        var guardada = reservaRepository.save(r);
        return toDTO(guardada);
    }

    public ReservaAdminDTO tipoCobroPorId(Long id, String tipoCobroStr) {
        var r = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        if (tipoCobroStr != null) {
            try {
                var tipoCobro = TipoCobro.valueOf(tipoCobroStr);
                r.setTipoCobro(tipoCobro);
            } catch (Exception e) {
                throw new IllegalArgumentException("Tipo de cobro inválido");
            }
        } else {
            r.setTipoCobro(null); // permite que quede vacío
        }
        var guardada = reservaRepository.save(r);
        return toDTO(guardada);
    }


    public ReservaAdminDTO actualizarObservaciones(Long id, String observaciones) {
        var r = reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        r.setObservaciones(observaciones); // Actualiza las observaciones
        var guardada = reservaRepository.save(r);

        return toDTO(guardada);
    }








}
