package com.alquiler.alquilerbicicletas.repositorios;



import com.alquiler.alquilerbicicletas.modelos.Accesorio;
import com.alquiler.alquilerbicicletas.modelos.Bicicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AccesorioRepository extends JpaRepository<Accesorio, Long> {

    // Buscar solo accesorios activos
    List<Accesorio> findByActivoTrue();



    @Query("""
       SELECT r.accesorio.id, COALESCE(SUM(r.cantidad), 0)
       FROM ReservaAccesorio r
       WHERE r.accesorio.id IN :accesorioIds
         AND r.reserva.fechaInicio <= :fechaFin
         AND r.reserva.fechaFin >= :fechaInicio
         AND r.reserva.estado IN (
           com.alquiler.alquilerbicicletas.enumerados.EstadoReserva.CONFIRMADA,
           com.alquiler.alquilerbicicletas.enumerados.EstadoReserva.PENDIENTE
         )
       GROUP BY r.accesorio.id
   """)
    List<Object[]> obtenerCantidadReservadaPorAccesorios(
            @Param("accesorioIds") List<Long> accesorioIds,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );


    @Query("SELECT a FROM Accesorio a WHERE " +
            "a.activo = true AND " +
            "NOT EXISTS (" +
            "   SELECT 1 FROM ReservaAccesorio ra JOIN ra.reserva r " +
            "   WHERE ra.accesorio = a AND " +
            "   (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio) AND " +
            "   r.estado IN (" +
            "       com.alquiler.alquilerbicicletas.enumerados.EstadoReserva.CONFIRMADA," +
            "       com.alquiler.alquilerbicicletas.enumerados.EstadoReserva.PENDIENTE" +
            "   )" +
            ")")
    List<Accesorio> findAccesoriosDisponibles(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}



