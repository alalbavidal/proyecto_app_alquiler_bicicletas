package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "tarifa", schema = "alquiler_bicicletas")
public class Tarifa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, length = 100)
    private String descripcion;

    @Column(nullable = false)
    private Double precio;

    @Column(name = "horas_incluidas", nullable = false)
    private Integer horasIncluidas;

    @Column(name = "dias_minimos", nullable = false)
    private Integer diasMinimos;

    @Column(name = "precio_por_dia", nullable = false)
    private boolean precioPorDia;

    @Column(nullable = false)
    private boolean activa;

    @Column(name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(name = "hora_fin")
    private LocalTime horaFin;



}
