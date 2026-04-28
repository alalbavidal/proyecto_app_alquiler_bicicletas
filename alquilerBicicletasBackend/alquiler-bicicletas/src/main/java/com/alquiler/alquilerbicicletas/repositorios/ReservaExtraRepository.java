package com.alquiler.alquilerbicicletas.repositorios;

import com.alquiler.alquilerbicicletas.modelos.ReservaExtra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaExtraRepository extends JpaRepository<ReservaExtra, Long> {
    List<ReservaExtra> findByReservaId(Long reservaId);
}
