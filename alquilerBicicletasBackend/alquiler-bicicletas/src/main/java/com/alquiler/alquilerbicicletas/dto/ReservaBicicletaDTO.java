package com.alquiler.alquilerbicicletas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaBicicletaDTO {
    private Long bicicletaId;
    private String nombre; // <- Nuevo campo

    public ReservaBicicletaDTO(Long id) {
    }

    @Override
    public String toString() {
        return nombre != null ? nombre + " (ID: " + bicicletaId + ")" : "Bicicleta ID: " + bicicletaId;
    }
}

