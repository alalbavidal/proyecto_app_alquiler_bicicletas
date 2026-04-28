package com.alquiler.alquilerbicicletas.adm;

import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class AdminDiaResumenDTO {
    private LocalDate fecha;
    private Integer totalBicis;
    private Integer bicisReservadas;
    private Integer bicisLibres;
    private List<AdminReservaListItemDTO> reservas;
}

