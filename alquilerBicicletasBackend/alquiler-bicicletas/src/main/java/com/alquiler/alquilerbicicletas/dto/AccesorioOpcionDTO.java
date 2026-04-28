package com.alquiler.alquilerbicicletas.dto;

import lombok.*;

@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class AccesorioOpcionDTO {
    private Long id;
    private String nombre;
    private Double precioDia;
    private Integer stockDisponible; // stock - reservado en el intervalo
    private Integer maxPorReserva;   // min(stockDisponible, numeroBicis)
}
