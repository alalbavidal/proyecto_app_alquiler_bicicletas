package com.alquiler.alquilerbicicletas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDTO {

    private Long clienteId;
    private Long tarifaId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private List<ReservaBicicletaDTO> bicicletas;
    private List<ReservaAccesorioDTO> accesorios;


}
