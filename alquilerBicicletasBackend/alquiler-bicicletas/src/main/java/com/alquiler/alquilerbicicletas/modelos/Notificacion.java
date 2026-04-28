package com.alquiler.alquilerbicicletas.modelos;

import com.alquiler.alquilerbicicletas.enumerados.EstadoProducto;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notificacion", schema = "alquiler_bicicletas")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "reserva_id", nullable = false)
    private Reserva reserva;

    @Column(nullable = false, length = 50)
    private String tipo; // 'confirmacion', 'recordatorio', 'cancelacion'

    @Column(columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "fecha_envio", nullable = false)
    private LocalDateTime fechaEnvio;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private EstadoProducto estadoProducto; // 'pendiente', 'enviado', 'fallido'
}