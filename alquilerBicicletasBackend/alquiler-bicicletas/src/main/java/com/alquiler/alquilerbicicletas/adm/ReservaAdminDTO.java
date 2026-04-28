// src/main/java/com/alquiler/alquilerbicicletas/adm/ReservaAdminDTO.java
package com.alquiler.alquilerbicicletas.adm;

import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.enumerados.TipoCobro;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReservaAdminDTO {
    private Long id;
    private String clienteNombre;
    private String clienteApellido;
    private String clienteEmail;
    private String clienteTelefono;
    private LocalDateTime fechaInicio;
    private LocalDateTime fechaFin;
    private BigDecimal precioTotal;
    private BigDecimal extrasTotal;
    private EstadoReserva estado;
    private boolean pagado;
    private String contratoUrl;
    private EstadoReserva estadoActual;
    private TipoCobro tipoCobro;
    private String observaciones;
}
