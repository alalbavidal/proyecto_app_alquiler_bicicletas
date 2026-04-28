package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.modelos.Tarifa;
import com.alquiler.alquilerbicicletas.repositorios.TarifaRepository;
import com.alquiler.alquilerbicicletas.enumerados.TipoTarifa;
import com.alquiler.alquilerbicicletas.util.RangoFechas;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
public class TarifaService {

    @Autowired
    private TarifaRepository tarifaRepository;

    /**
     * Valida si la tarifa es válida para la reserva.
     *
     * @param tarifaId el ID de la tarifa a validar.
     * @return la tarifa si es válida.
     * @throws IllegalArgumentException si la tarifa no es válida.
     */
    public Tarifa validarTarifa(Long tarifaId) {
        Tarifa tarifa = tarifaRepository.findById(tarifaId)
                .orElseThrow(() -> new IllegalArgumentException("Tarifa con ID " + tarifaId + " no encontrada"));

        if (tarifa.getPrecio() == null || tarifa.getPrecio() <= 0) {
            throw new IllegalArgumentException("La tarifa con ID " + tarifaId + " tiene un precio inválido.");
        }

        if (Boolean.TRUE.equals(tarifa.isPrecioPorDia())) {
            if (tarifa.getDiasMinimos() == null || tarifa.getDiasMinimos() < 1) {
                throw new IllegalArgumentException("La tarifa con ID " + tarifaId + " debe indicar días mínimos ≥ 1.");
            }
            // horasIncluidas puede estar informativo (72/168) pero no lo usamos para el rango
        } else {
            // Por horas: o bien tenemos horasIncluidas, o bien (horaInicio y horaFin)
            boolean tieneHorasIncluidas = tarifa.getHorasIncluidas() != null && tarifa.getHorasIncluidas() > 0;
            boolean tieneHorasFijas = tarifa.getHoraInicio() != null && tarifa.getHoraFin() != null;

            if (!tieneHorasIncluidas && !tieneHorasFijas) {
                throw new IllegalArgumentException("La tarifa con ID " + tarifaId + " debe tener horasIncluidas o horas de inicio/fin.");
            }
        }

        return tarifa;
    }



    // TarifaService.java
    public RangoFechas calcularRangoFechas(LocalDate fechaBase, Tarifa tarifa) {
        LocalDateTime inicio;
        LocalDateTime fin;

        if (Boolean.TRUE.equals(tarifa.isPrecioPorDia())) {
            // Por días completos: 09:00 -> 09:00 (o usa la horaInicio si la has definido en BBDD)
            LocalTime hi = (tarifa.getHoraInicio() != null) ? tarifa.getHoraInicio() : LocalTime.of(9, 0);
            int dias = (tarifa.getDiasMinimos() != null && tarifa.getDiasMinimos() > 0) ? tarifa.getDiasMinimos() : 1;

            inicio = fechaBase.atTime(hi);
            // Fin EXACTO a la misma hora tras N días (sin "-1s")
            fin = inicio.plusDays(dias);
            return new RangoFechas(inicio, fin);
        }

        // Por horas (mañana/tarde/medio día/día completo 24h con horas)
        LocalTime hi = (tarifa.getHoraInicio() != null) ? tarifa.getHoraInicio() : LocalTime.of(9, 0);
        LocalTime hf = tarifa.getHoraFin(); // puede ser null
        inicio = fechaBase.atTime(hi);

        if (hf != null) {
            fin = fechaBase.atTime(hf);
            // Si fin no es posterior a inicio, consideramos que cruza medianoche o 24h exactas
            if (!hf.isAfter(hi)) {
                fin = fin.plusDays(1);
            }
        } else {
            int horas = (tarifa.getHorasIncluidas() != null && tarifa.getHorasIncluidas() > 0) ? tarifa.getHorasIncluidas() : 1;
            fin = inicio.plusHours(horas);
        }

        return new RangoFechas(inicio, fin);
    }




    public List<Tarifa> findAll() {
        return tarifaRepository.findAll();
    }








}
