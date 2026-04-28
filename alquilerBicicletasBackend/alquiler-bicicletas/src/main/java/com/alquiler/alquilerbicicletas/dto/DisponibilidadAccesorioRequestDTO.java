package com.alquiler.alquilerbicicletas.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class DisponibilidadAccesorioRequestDTO {
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<AccesorioCantidadDTO> accesorios;

    @Data
    public static class AccesorioCantidadDTO {
        private Long accesorioId;
        private int cantidad;
    }
}
