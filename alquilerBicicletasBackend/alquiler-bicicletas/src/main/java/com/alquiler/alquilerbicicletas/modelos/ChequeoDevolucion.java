package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.alquiler.alquilerbicicletas.enumerados.EstadoProducto;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "chequeo_devolucion", schema = "alquiler_bicicletas")
public class ChequeoDevolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne
    @JoinColumn(name = "empleado_id", nullable = false)
    private Empleado empleado;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_producto")
    private EstadoProducto estadoProducto;

    @Column(name = "fecha", nullable = false)
    private LocalDateTime fecha;
}