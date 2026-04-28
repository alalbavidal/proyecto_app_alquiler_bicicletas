// src/main/java/com/alquiler/alquilerbicicletas/servicios/TarifaAdminService.java
package com.alquiler.alquilerbicicletas.adm;

import com.alquiler.alquilerbicicletas.dto.TarifaDTO;
import com.alquiler.alquilerbicicletas.modelos.Tarifa;
import com.alquiler.alquilerbicicletas.repositorios.TarifaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TarifaAdminService {

    private final TarifaRepository repo;

    @Transactional(readOnly = true)
    public List<Tarifa> listar() {
        return repo.findAll();
    }

    @Transactional(readOnly = true)
    public Tarifa obtener(Long id) {
        return repo.findById(id).orElseThrow(() -> new IllegalArgumentException("Tarifa no encontrada"));
    }

    @Transactional
    public Tarifa crearOActualizar(TarifaDTO dto, Long idNullable) {
        validar(dto);

        final Tarifa t = (idNullable == null) ? new Tarifa() : obtener(idNullable);
        t.setNombre(dto.getNombre());
        t.setDescripcion(dto.getDescripcion());
        t.setPrecio(dto.getPrecio());
        t.setHorasIncluidas(dto.getHorasIncluidas());
        t.setDiasMinimos(dto.getDiasMinimos());
        t.setPrecioPorDia(Boolean.TRUE.equals(dto.getPrecioPorDia()));
        t.setActiva(Boolean.TRUE.equals(dto.getActiva()));
        t.setHoraInicio(dto.getHoraInicio());
        t.setHoraFin(dto.getHoraFin());

        return repo.save(t);
    }

    @Transactional
    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    @Transactional
    public Tarifa setActiva(Long id, boolean activa) {
        Tarifa t = obtener(id);
        t.setActiva(activa);
        return repo.save(t);
    }

    private void validar(TarifaDTO d) {
        if (d.getNombre() == null || d.getNombre().isBlank())
            throw new IllegalArgumentException("El nombre es obligatorio");

        if (d.getPrecio() == null || d.getPrecio() <= 0)
            throw new IllegalArgumentException("El precio debe ser > 0");

        if (Boolean.TRUE.equals(d.getPrecioPorDia())) {
            // por días -> exigir días mínimos >=1; horasIncluidas puede ser informativo
            if (d.getDiasMinimos() == null || d.getDiasMinimos() < 1)
                throw new IllegalArgumentException("Para tarifas por día, 'días mínimos' debe ser ≥ 1");
            // horaInicio/horaFin opcionales (si quieres fijar a 09:00:00, hazlo en cálculo)
        } else {
            // por horas -> o bien horasIncluidas > 0, o bien horaInicio & horaFin
            boolean tieneHorasIncluidas = d.getHorasIncluidas() != null && d.getHorasIncluidas() > 0;
            boolean tieneHorarioFijo = d.getHoraInicio() != null && d.getHoraFin() != null;
            if (!tieneHorasIncluidas && !tieneHorarioFijo)
                throw new IllegalArgumentException("Para tarifas por horas debes indicar 'horasIncluidas' o un rango 'horaInicio/horaFin'");
        }
    }
}
