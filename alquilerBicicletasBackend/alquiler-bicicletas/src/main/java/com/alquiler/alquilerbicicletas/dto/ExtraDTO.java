package com.alquiler.alquilerbicicletas.dto;

import com.alquiler.alquilerbicicletas.enumerados.AplicableExtra;
import lombok.*;

@Data @AllArgsConstructor @NoArgsConstructor @Builder
public class ExtraDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private Double precio;
    private AplicableExtra aplicableA;
    private String restricciones;

    public ExtraDTO(Long id, String nombre, String descripcion, Double precio) {
        this.id = id;
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.precio = precio;
    }

}
