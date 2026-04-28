package com.alquiler.alquilerbicicletas.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccesorioDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Double precioDia;
    private Integer stock;
    private String observaciones;
    private Boolean activo;
}
