package com.alquiler.alquilerbicicletas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaConClienteDTO {
    private ClienteDTO cliente;
    private ReservaDTO reserva;
}
