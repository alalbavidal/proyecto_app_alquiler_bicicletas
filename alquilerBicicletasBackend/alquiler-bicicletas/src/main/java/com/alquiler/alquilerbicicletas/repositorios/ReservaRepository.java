package com.alquiler.alquilerbicicletas.repositorios;

import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.modelos.ReservaExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.alquiler.alquilerbicicletas.modelos.Reserva;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long>, JpaSpecificationExecutor<Reserva> {

    //List<ReservaExtra> findByReservaId(Long reservaId);


    // Para controlar conflictos con bicicletas
    @Query("SELECT r FROM Reserva r JOIN r.bicicletas rb " +
            "WHERE rb.bicicleta.id = :bicicletaId " +
            "AND r.estado <> 'CANCELADA' " +
            "AND r.fechaInicio < :fechaFin " +
            "AND r.fechaFin > :fechaInicio")
    List<Reserva> findReservasConflicto(Long bicicletaId, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    Optional<Reserva> findByCancelToken(String cancelToken);

    @Query("""
   SELECT r FROM Reserva r
   JOIN FETCH r.cliente c
   LEFT JOIN FETCH r.bicicletas rb
   WHERE r.fechaInicio <= :fin AND r.fechaFin >= :ini
   ORDER BY r.fechaInicio ASC
""")
    List<Reserva> findByRango(@Param("ini") LocalDateTime ini, @Param("fin") LocalDateTime fin);

    @Query("""
   SELECT DISTINCT r FROM Reserva r
   JOIN r.cliente c
   LEFT JOIN r.bicicletas rb
   WHERE r.fechaInicio <= :fin AND r.fechaFin >= :ini
     AND (LOWER(c.email) LIKE :q OR LOWER(c.nombre) LIKE :q OR LOWER(c.apellido) LIKE :q)
   ORDER BY r.fechaInicio ASC
""")
    List<Reserva> findByRangoAndQuery(@Param("ini") LocalDateTime ini, @Param("fin") LocalDateTime fin, @Param("q") String q);

    List<Reserva> findAllByOrderByFechaInicioDesc();



    // Mantener método para conflictos de reservas
    List<Reserva> findByBicicletasBicicletaIdAndEstadoNotAndFechaInicioLessThanAndFechaFinGreaterThan(
            Long bicicletas_bicicleta_id, EstadoReserva estado, LocalDateTime fechaInicio, LocalDateTime fechaFin
    );














}
