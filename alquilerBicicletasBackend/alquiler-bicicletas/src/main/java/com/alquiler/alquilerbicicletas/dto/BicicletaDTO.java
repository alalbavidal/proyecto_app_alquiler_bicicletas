package com.alquiler.alquilerbicicletas.dto;

import com.alquiler.alquilerbicicletas.enumerados.EstadoBicicleta;
import com.alquiler.alquilerbicicletas.modelos.Bicicleta;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BicicletaDTO {
    private Long id;
    private String modelo;
    private String numero;
    private String bastidor;
    private String candado;
    private String claveCandado;
    private String descripcion;
    private String imagenUrl;
    private EstadoBicicleta estado;
    private String observaciones;


    // Constructor para conversión desde entidad
    public BicicletaDTO(Bicicleta bicicleta) {
        this.id = bicicleta.getId();
        this.modelo = bicicleta.getModelo();
        this.numero = bicicleta.getNumero();
        this.bastidor = bicicleta.getBastidor();
        this.candado = bicicleta.getCandado();
        this.claveCandado = bicicleta.getClaveCandado();
        this.descripcion = bicicleta.getDescripcion();
        this.imagenUrl = bicicleta.getImagenUrl();
        this.estado = bicicleta.getEstado();
        this.observaciones = bicicleta.getObservaciones();
    }
}