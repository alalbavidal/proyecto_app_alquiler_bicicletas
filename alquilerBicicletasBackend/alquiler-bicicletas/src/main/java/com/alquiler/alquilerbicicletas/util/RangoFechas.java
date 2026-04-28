package com.alquiler.alquilerbicicletas.util;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class RangoFechas {
    private final LocalDateTime fechaInicio;
    private final LocalDateTime fechaFin;

    public RangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
    }
}
