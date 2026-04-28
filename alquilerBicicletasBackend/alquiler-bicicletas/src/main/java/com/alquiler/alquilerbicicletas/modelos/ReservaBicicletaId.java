package com.alquiler.alquilerbicicletas.modelos;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
@Embeddable
public class ReservaBicicletaId implements Serializable {

    // Getters y setters
    @Column(name = "reserva_id")
    private Long reservaId;
    @Column(name = "bicicleta_id")
    private Long bicicletaId;

    @Serial
    private static final long serialVersionUID = 1L;

    // Constructor, getters y setters

    public ReservaBicicletaId() {}

    public ReservaBicicletaId(Long reservaId, Long bicicletaId) {
        this.reservaId = reservaId;
        this.bicicletaId = bicicletaId;
    }

    // hashCode y equals (importante para las claves compuestas)

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ReservaBicicletaId that = (ReservaBicicletaId) o;
        return Objects.equals(reservaId, that.reservaId) &&
                Objects.equals(bicicletaId, that.bicicletaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(reservaId, bicicletaId);
    }

}
