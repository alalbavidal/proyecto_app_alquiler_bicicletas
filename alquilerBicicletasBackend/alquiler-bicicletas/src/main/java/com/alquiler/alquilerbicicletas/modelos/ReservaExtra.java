package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "reserva_extra", schema = "alquiler_bicicletas")
public class ReservaExtra {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "extra_id", nullable = false)
    private Extra extra;

    @Column(nullable = false)
    private Integer cantidad;

    @Column(nullable = false)
    private Double precioTotal;

    @Column(nullable = false)
    private LocalDateTime fechaAdicion;

    @Column(nullable = false)
    private Boolean pagado = false;

    @PrePersist
    protected void onCreate() {
        this.fechaAdicion = LocalDateTime.now();
    }
}
