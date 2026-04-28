package com.alquiler.alquilerbicicletas.dto;

import com.alquiler.alquilerbicicletas.modelos.Bicicleta;
import com.alquiler.alquilerbicicletas.modelos.Tarifa;
import com.alquiler.alquilerbicicletas.servicios.BicicletaService;
import lombok.Builder;


import java.time.LocalDateTime;

@Builder
public record BicicletaDisponibleDTO(
        BicicletaDTO bicicleta,
        double precioTarifa,
        String horarioTarifa, // Ej: "TARDE (17:00-21:00)"
        LocalDateTime fechaInicio,
        LocalDateTime fechaFin
) {}



