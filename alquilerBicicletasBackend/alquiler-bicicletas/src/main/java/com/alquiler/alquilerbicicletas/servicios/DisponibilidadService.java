package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.dto.DiaNoDisponibleDTO;
import com.alquiler.alquilerbicicletas.repositorios.BicicletaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class DisponibilidadService {

    @Autowired
    private BicicletaRepository bicicletaRepository;

    public List<DiaNoDisponibleDTO> obtenerDiasNoDisponibles(int mes, int anio) {
        List<DiaNoDisponibleDTO> dias = new ArrayList<>();
        LocalDate inicio = LocalDate.of(anio, mes, 1);
        LocalDate fin = inicio.withDayOfMonth(inicio.lengthOfMonth());

        for (LocalDate fecha = inicio; !fecha.isAfter(fin); fecha = fecha.plusDays(1)) {
            if (fecha.getDayOfWeek() == DayOfWeek.SUNDAY) {
                dias.add(new DiaNoDisponibleDTO(fecha, "DOMINGO"));
            } else if (!hayBicicletasDisponibles(fecha)) {
                dias.add(new DiaNoDisponibleDTO(fecha, "SIN_BICICLETAS"));
            }
        }

        return dias;
    }

    private boolean hayBicicletasDisponibles(LocalDate fecha) {
        LocalDateTime inicio = fecha.atStartOfDay();
        LocalDateTime fin = fecha.atTime(LocalTime.MAX);
        return !bicicletaRepository.findBicicletasDisponibles(inicio, fin).isEmpty();
    }
}
