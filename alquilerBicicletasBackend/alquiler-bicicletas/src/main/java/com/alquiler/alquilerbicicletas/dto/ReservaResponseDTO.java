package com.alquiler.alquilerbicicletas.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Generated
public class ReservaResponseDTO {
    private Long id;
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
