package com.alquiler.alquilerbicicletas.util;

import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class ReservaSpecs {

    public static Specification<Reserva> conClienteLike(String q) {
        return (root, query, cb) -> {
            if (q == null || q.isBlank()) return null;
            String like = "%" + q.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("cliente").get("nombre")), like),
                    cb.like(cb.lower(root.get("cliente").get("apellido")), like),
                    cb.like(cb.lower(root.get("cliente").get("email")), like),
                    cb.like(cb.lower(root.get("cliente").get("telefono")), like)
            );
        };
    }

    public static Specification<Reserva> conEstado(EstadoReserva estado) {
        return (root, query, cb) -> estado == null ? null : cb.equal(root.get("estado"), estado);
    }

    public static Specification<Reserva> conPagado(Boolean pagado) {
        return (root, query, cb) -> pagado == null ? null : cb.equal(root.get("pagado"), pagado);
    }

    public static Specification<Reserva> conFechaInicioFin(LocalDateTime ini, LocalDateTime fin) {
        return (root, query, cb) -> {
            LocalDateTime ahora = LocalDateTime.now();

            // Si no se pasa ninguna fecha, mostrar solo reservas futuras
            if (ini == null && fin == null) {
                return cb.greaterThanOrEqualTo(root.get("fechaFin"), ahora);
            }

            // Si se pasan ambas fechas
            if (ini != null && fin != null) {
                return cb.and(
                        cb.greaterThanOrEqualTo(root.get("fechaFin"), ini),
                        cb.lessThanOrEqualTo(root.get("fechaInicio"), fin)
                );
            }

            // Solo fecha de inicio
            if (ini != null) {
                return cb.greaterThanOrEqualTo(root.get("fechaFin"), ini);
            }

            // Solo fecha de fin
            return cb.lessThanOrEqualTo(root.get("fechaInicio"), fin);
        };
    }

}
