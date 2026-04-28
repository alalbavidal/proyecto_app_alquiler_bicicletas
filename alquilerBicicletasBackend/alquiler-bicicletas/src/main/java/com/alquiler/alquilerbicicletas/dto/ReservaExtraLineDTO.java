package com.alquiler.alquilerbicicletas.dto;

import com.alquiler.alquilerbicicletas.modelos.ReservaExtra;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaExtraLineDTO {
    private Long id;
    private Long extraId;
    private String nombre;
    private Integer cantidad;
    private Double precioTotal;
    private Boolean pagado;
    private LocalDateTime fechaAdicion;

    public static ReservaExtraLineDTO from(ReservaExtra re) {
        ReservaExtraLineDTO dto = new ReservaExtraLineDTO();
        dto.id = re.getId();
        dto.extraId = re.getExtra() != null ? re.getExtra().getId() : null;
        dto.nombre = re.getExtra() != null ? re.getExtra().getNombre() : null;
        dto.cantidad = re.getCantidad();
        dto.precioTotal = re.getPrecioTotal();
        dto.pagado = re.getPagado();
        dto.fechaAdicion = re.getFechaAdicion();
        return dto;
    }
}
