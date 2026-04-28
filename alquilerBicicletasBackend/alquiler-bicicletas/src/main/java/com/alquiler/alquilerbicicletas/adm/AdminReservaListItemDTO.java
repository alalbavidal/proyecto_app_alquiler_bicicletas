package com.alquiler.alquilerbicicletas.adm;

import com.alquiler.alquilerbicicletas.modelos.Reserva;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AdminReservaListItemDTO {
    private Long id;
    private String clienteNombre;
    private String clienteEmail;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private String tarifaNombre;
    private Integer numBicis;
    private BigDecimal total;
    private String estado;

    public static AdminReservaListItemDTO from(Reserva r) {
        return AdminReservaListItemDTO.builder()
                .id(r.getId())
                .clienteNombre(r.getCliente().getNombre() + " " + r.getCliente().getApellido())
                .clienteEmail(r.getCliente().getEmail())
                .fechaInicio(r.getFechaInicio())
                .fechaFin(r.getFechaFin())
                .tarifaNombre(r.getTarifa().getNombre())
                .numBicis(r.getBicicletas() != null ? r.getBicicletas().size() : 0)
                .total(r.getPrecioTotal())
                .estado(r.getEstado().name())
                .build();
    }
}

