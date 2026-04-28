package com.alquiler.alquilerbicicletas.dto;

import lombok.*;
import java.time.LocalTime;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class TarifaDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private Integer horasIncluidas;
    private Integer diasMinimos;
    private Boolean precioPorDia;
    private Boolean activa;
    private LocalTime horaInicio;
    private LocalTime horaFin;
}
