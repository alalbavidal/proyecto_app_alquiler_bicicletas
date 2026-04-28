package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reserva_bicicleta", schema = "alquiler_bicicletas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"reserva", "bicicleta"})
public class ReservaBicicleta {

    @EmbeddedId
    private ReservaBicicletaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reservaId")
    @JoinColumn(name = "reserva_id")
    private Reserva reserva;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("bicicletaId")
    @JoinColumn(name = "bicicleta_id")
    private Bicicleta bicicleta;



}
