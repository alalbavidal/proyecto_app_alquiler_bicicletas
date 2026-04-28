package com.alquiler.alquilerbicicletas.dto;

import lombok.Data;

@Data
public class AplicarExtraRequest {
    private Long extraId;
    private Integer cantidad;
    // RESERVA | DEVOLUCION
    private String contexto;
}
