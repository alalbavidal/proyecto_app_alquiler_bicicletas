package com.alquiler.alquilerbicicletas.repositorios;

import com.alquiler.alquilerbicicletas.enumerados.AplicableExtra;
import com.alquiler.alquilerbicicletas.modelos.Extra;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExtraRepository extends JpaRepository<Extra, Long> {
    List<Extra> findByAplicableA(AplicableExtra aplicableA);
    List<Extra> findByNombreContainingIgnoreCase(String nombre);

}
