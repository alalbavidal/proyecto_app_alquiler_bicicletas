package com.alquiler.alquilerbicicletas.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaDetalleDTO {

    private Long clienteId;
    private Long tarifaId;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal precioTotal;
    private BigDecimal extrasTotal;
    private String estado;
    private String contratoUrl;
    private List<ReservaBicicletaDTO> bicicletas;
    private List<ReservaAccesorioDTO> accesorios;
    private String cancelToken;
}
