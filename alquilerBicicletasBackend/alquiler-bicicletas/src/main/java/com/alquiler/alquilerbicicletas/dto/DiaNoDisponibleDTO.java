package com.alquiler.alquilerbicicletas.dto;

import java.time.LocalDate;

public class DiaNoDisponibleDTO {
    private LocalDate fecha;
    private String motivo;

    public DiaNoDisponibleDTO(LocalDate fecha, String motivo) {
        this.fecha = fecha;
        this.motivo = motivo;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }
}
