package com.alquiler.alquilerbicicletas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;


@Data
@AllArgsConstructor
public class AccesorioDisponibilidadResponseDTO {
    private boolean disponible;
    private List<String> errores; // lista de mensajes tipo: "No hay suficiente stock para Casco Infantil"


}

