package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "informe_mensual", schema = "alquiler_bicicletas")
public class InformeMensual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(name = "total_reservas")
    private Integer totalReservas;

    @Column(name = "ingresos_totales")
    private Double ingresosTotales;

    @Column(name = "archivo_url", length = 255)
    private String archivoUrl;

    @Column(name = "fecha_generacion", nullable = false)
    private LocalDateTime fechaGeneracion;
}