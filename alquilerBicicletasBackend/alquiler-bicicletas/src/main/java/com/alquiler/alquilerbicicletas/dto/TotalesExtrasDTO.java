package com.alquiler.alquilerbicicletas.dto;

import com.alquiler.alquilerbicicletas.modelos.Reserva;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class TotalesExtrasDTO {
    private Long reservaId;
    private BigDecimal extrasTotal;          // Suma de todos los extras
    private List<ReservaExtraLineDTO> extras; // Lista de extras con cantidad y precio

    public TotalesExtrasDTO(Reserva r) {
        this.reservaId = r.getId();
        this.extrasTotal = r.getExtrasTotal();
        this.extras = r.getExtras().stream()
                .map(ReservaExtraLineDTO::from)
                .toList();
    }
}
