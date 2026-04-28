package com.alquiler.alquilerbicicletas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaAccesorioDTO {
    private Long accesorioId;
    private Integer cantidad;
    private BigDecimal precioTotal;
    private String nombre; // <- Nuevo campo

    public ReservaAccesorioDTO(Long id, Integer cantidad) {
    }


    @Override
    public String toString() {
        return "Accesorio ID: " + accesorioId + ", Cantidad: " + cantidad +
                (precioTotal != null ? ", Total: %.2f €".formatted(precioTotal) : "");
    }

    public String getNombre() {
        return nombre != null ? nombre : "Accesorio ID: " + accesorioId;
    }
}
