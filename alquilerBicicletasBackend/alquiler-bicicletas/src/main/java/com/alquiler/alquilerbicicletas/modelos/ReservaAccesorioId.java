package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;


@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Embeddable
public class ReservaAccesorioId implements Serializable {
    @EqualsAndHashCode.Include
    private Long reservaId;

    @EqualsAndHashCode.Include
    private Long accesorioId;



}