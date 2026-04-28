package com.alquiler.alquilerbicicletas.enumerados;

import lombok.Getter;

@Getter
public enum TipoTarifa {
    MAÑANA("De 9:00 a 14:00", 8.0, 5, 1, false),  // 5 horas, 1 día mínimo, no precio por día
    TARDE("De 17:00 a 21:00", 8.0, 4, 1, false),   // 4 horas, 1 día mínimo, no precio por día
    MEDIO_DIA("12 horas (9:00-21:00)", 12.0, 12, 1, false),
    DIA_COMPLETO("24 horas", 15.0, 24, 1, false),
    TRES_DIAS("3 días (10€/día)", 30.0, 72, 3, true),  // 3 días x 24 horas, 3 días mínimos, precio por día
    SEMANA("7 días (8€/día)", 56.0, 168, 7, true);     // 7 días x 24 horas, 7 días mínimos, precio por día

    private final String descripcion;
    private final double precio;
    private final int horasCubiertas;
    private final int diasMinimos;   // Días mínimos para cada tarifa
    private final boolean precioPorDia; // Indica si la tarifa tiene precio por día

    TipoTarifa(String descripcion, double precio, int horasCubiertas, int diasMinimos, boolean precioPorDia) {
        this.descripcion = descripcion;
        this.precio = precio;
        this.horasCubiertas = horasCubiertas;
        this.diasMinimos = diasMinimos;
        this.precioPorDia = precioPorDia;
    }

    public int getDiasMinimos() {
        return diasMinimos;
    }

    public boolean isPrecioPorDia() {
        return precioPorDia;
    }
    public String getDescripcion() {
        return this.descripcion;
    }


}
