package com.alquiler.alquilerbicicletas.repositorios;

import com.alquiler.alquilerbicicletas.enumerados.TipoTarifa;
import com.alquiler.alquilerbicicletas.modelos.Tarifa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface TarifaRepository extends JpaRepository<Tarifa, Long> {


    }
