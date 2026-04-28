package com.alquiler.alquilerbicicletas.repositorios;

import com.alquiler.alquilerbicicletas.modelos.Bicicleta;
import com.alquiler.alquilerbicicletas.enumerados.EstadoBicicleta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BicicletaRepository extends JpaRepository<Bicicleta, Long> {

    // Métodos básicos automáticos proporcionados por JpaRepository:
    // - save(), findById(), findAll(), deleteById(), etc.

    // Buscar por número único
    Optional<Bicicleta> findByNumero(String numero);

    // Buscar por número de bastidor
    Optional<Bicicleta> findByBastidor(String bastidor);

    // Buscar por estado
    List<Bicicleta> findByEstado(EstadoBicicleta estado);

    // Buscar por modelo (contiene, case insensitive)
    List<Bicicleta> findByModeloContainingIgnoreCase(String modelo);

    // Buscar bicicletas disponibles para alquiler (ACTIVO y no reservadas en un rango de fechas)


    // Contar bicicletas por estado
    @Query("SELECT b.estado, COUNT(b) FROM Bicicleta b GROUP BY b.estado")
    List<Object[]> countByEstado();

    boolean existsByBastidor(String bastidor);

    boolean existsByNumero(String numero);

    @Modifying
    @Query("UPDATE Bicicleta b SET b.estado = 'BAJA', b.fechaBaja = CURRENT_TIMESTAMP WHERE b.id = :id AND b.estado != 'BAJA'")
    int softDeleteById(@Param("id") Long id);


    /**
     * Encuentra bicicletas disponibles para un rango de fechas específico.
     * Considera:
     * 1. Que la bicicleta esté ACTIVA (no EN_REPARACION ni BAJA)
     * 2. Que no tenga reservas CONFIRMADAS o PENDIENTES en ese rango
     */
    @Query("SELECT b FROM Bicicleta b WHERE " +
            "b.estado = com.alquiler.alquilerbicicletas.enumerados.EstadoBicicleta.ACTIVO AND " +
            "NOT EXISTS (" +
            "   SELECT 1 FROM ReservaBicicleta rb JOIN rb.reserva r " +
            "   WHERE rb.bicicleta = b AND " +
            "   (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio) AND " +
            "   r.estado IN (" +
            "       com.alquiler.alquilerbicicletas.enumerados.EstadoReserva.CONFIRMADA," +
            "       com.alquiler.alquilerbicicletas.enumerados.EstadoReserva.PENDIENTE" +
            "   )" +
            ")")
    List<Bicicleta> findBicicletasDisponibles(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);

    /**
     * Método alternativo con JOIN explícito para mejor rendimiento en algunos casos
     */
   @Query("SELECT DISTINCT b FROM Bicicleta b " +
   "LEFT JOIN ReservaBicicleta rb ON rb.bicicleta = b " +
   "LEFT JOIN rb.reserva r ON (r.fechaInicio <= :fechaFin AND r.fechaFin >= :fechaInicio AND " +
   "r.estado IN ('CONFIRMADA', 'PENDIENTE')) " +
   "WHERE b.estado = 'ACTIVO' AND " +
   "(rb.id IS NULL OR r.id IS NULL)")
    List<Bicicleta> findBicicletasDisponiblesAlternative(
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin);
}