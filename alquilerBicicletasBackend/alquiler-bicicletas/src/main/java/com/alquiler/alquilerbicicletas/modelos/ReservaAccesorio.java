package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reserva_accesorio", schema = "alquiler_bicicletas")
public class ReservaAccesorio {

    @EmbeddedId
    private ReservaAccesorioId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reservaId")
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("accesorioId")
    @JoinColumn(name = "accesorio_id")
    private Accesorio accesorio;

    @Column(nullable = false)
    private Integer cantidad; // Cantidad de este accesorio en la reserva

    @Column(name = "precio_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal precioTotal;

}