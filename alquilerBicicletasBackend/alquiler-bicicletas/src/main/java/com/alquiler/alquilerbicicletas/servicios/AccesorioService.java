package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.dto.*;
import com.alquiler.alquilerbicicletas.modelos.Accesorio;
import com.alquiler.alquilerbicicletas.repositorios.AccesorioRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;


import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AccesorioService {

    private final AccesorioRepository accesorioRepository;

    public List<AccesorioDTO> listarAccesoriosActivos() {
        return accesorioRepository.findByActivoTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public Optional<AccesorioDTO> obtenerPorId(Long id) {
        return accesorioRepository.findById(id)
                .map(this::toDTO);
    }

    public AccesorioDTO crear(AccesorioDTO dto) {
        Accesorio accesorio = toEntity(dto);
        accesorio.setActivo(true); // Asegurar que se cree como activo
        return toDTO(accesorioRepository.save(accesorio));
    }

    public Optional<AccesorioDTO> actualizar(Long id, AccesorioDTO dto) {
        return accesorioRepository.findById(id).map(accesorioExistente -> {
            accesorioExistente.setNombre(dto.getNombre());
            accesorioExistente.setDescripcion(dto.getDescripcion());
            accesorioExistente.setPrecioDia(BigDecimal.valueOf(dto.getPrecioDia()));
            accesorioExistente.setStock(dto.getStock());
            accesorioExistente.setObservaciones(dto.getObservaciones());
            accesorioExistente.setActivo(dto.getActivo());
            return toDTO(accesorioRepository.save(accesorioExistente));
        });
    }

    public boolean eliminarLogicamente(Long id) {
        return accesorioRepository.findById(id).map(accesorio -> {
            accesorio.setActivo(false);
            accesorioRepository.save(accesorio);
            return true;
        }).orElse(false);
    }

    // Métodos auxiliares de conversión

    private AccesorioDTO toDTO(Accesorio accesorio) {
        return AccesorioDTO.builder()
                .id(accesorio.getId())
                .nombre(accesorio.getNombre())
                .descripcion(accesorio.getDescripcion())
                .precioDia(accesorio.getPrecioDia().doubleValue())
                .stock(accesorio.getStock())
                .observaciones(accesorio.getObservaciones())
                .activo(accesorio.getActivo())
                .build();
    }

    private Accesorio toEntity(AccesorioDTO dto) {
        return Accesorio.builder()
                .id(dto.getId())
                .nombre(dto.getNombre())
                .descripcion(dto.getDescripcion())
                .precioDia(BigDecimal.valueOf(dto.getPrecioDia()))
                .stock(dto.getStock())
                .observaciones(dto.getObservaciones())
                .activo(dto.getActivo() != null ? dto.getActivo() : true)
                .build();
    }


    public List<AccesorioDTO> filtrar(String nombre, Boolean activo, Boolean disponibleStock) {
        List<Accesorio> accesorios = accesorioRepository.findAll();

        return accesorios.stream()
                .filter(a -> nombre == null || a.getNombre().toLowerCase().contains(nombre.toLowerCase()))
                .filter(a -> activo == null || a.getActivo().equals(activo))
                .filter(a -> disponibleStock == null || (disponibleStock && a.getStock() > 0))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }


   @Transactional(readOnly = true)
   public List<AccesorioDTO> findAccesoriosDisponibles(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
       return accesorioRepository.findAccesoriosDisponibles(fechaInicio, fechaFin).stream()
               .map(this::toDTO)
               .collect(Collectors.toList());
   }


    /**
     * Verifica la disponibilidad de los accesorios en el rango de fechas especificado.
     *
     * @param request Contiene la lista de accesorios y las fechas de inicio y fin.
     * @return Un objeto AccesorioDisponibilidadResponseDTO que indica si todos los accesorios están disponibles
     *         y una lista de mensajes con detalles sobre la disponibilidad.
     */

    public AccesorioDisponibilidadResponseDTO verificarDisponibilidadAccesorios(DisponibilidadAccesorioRequestDTO request) {
        List<String> mensajes = new ArrayList<>();

        LocalDateTime fechaInicio = request.getFechaInicio();
        LocalDateTime fechaFin = request.getFechaFin();

        if (fechaInicio.isAfter(fechaFin)) {
            mensajes.add("La fecha de inicio no puede ser posterior a la fecha de fin.");
            return new AccesorioDisponibilidadResponseDTO(false, mensajes);
        }

        List<Long> accesorioIds = request.getAccesorios().stream()
                .map(DisponibilidadAccesorioRequestDTO.AccesorioCantidadDTO::getAccesorioId)
                .collect(Collectors.toList());

        List<Object[]> cantidadesReservadas = accesorioRepository.obtenerCantidadReservadaPorAccesorios(accesorioIds, fechaInicio, fechaFin);
        Map<Long, Integer> reservasMap = cantidadesReservadas.stream()
                .collect(Collectors.toMap(
                        row -> (Long) row[0],
                        row -> ((Number) row[1]).intValue()
                ));

        boolean todosDisponibles = true;

        for (DisponibilidadAccesorioRequestDTO.AccesorioCantidadDTO item : request.getAccesorios()) {
            Long accesorioId = item.getAccesorioId();
            int cantidadSolicitada = item.getCantidad();

            Optional<Accesorio> accesorioOpt = accesorioRepository.findById(accesorioId);
            if (accesorioOpt.isEmpty()) {
                mensajes.add("Accesorio con ID " + accesorioId + " no encontrado.");
                todosDisponibles = false;
                continue;
            }

            Accesorio accesorio = accesorioOpt.get();
            int cantidadReservada = reservasMap.getOrDefault(accesorioId, 0);
            int disponible = accesorio.getStock() - cantidadReservada;

            if (cantidadSolicitada > disponible) {
                mensajes.add("No hay suficiente stock para " + accesorio.getNombre() +
                        ": solicitado " + cantidadSolicitada + ", disponible " + Math.max(disponible, 0));
                todosDisponibles = false;
            } else {
                mensajes.add("Accesorio disponible: " + accesorio.getNombre() +
                        " - solicitado " + cantidadSolicitada + ", disponible " + disponible);
            }
        }

        return new AccesorioDisponibilidadResponseDTO(todosDisponibles, mensajes);
    }


    // AccesorioService.java
    @Transactional(readOnly = true)
    public List<AccesorioOpcionDTO> calcularOpcionesAccesorios(
            LocalDateTime fechaInicio,
            LocalDateTime fechaFin,
            int numeroBicis
    ) {
        var activos = accesorioRepository.findByActivoTrue();
        if (activos.isEmpty()) return List.of();

        var ids = activos.stream().map(Accesorio::getId).toList();
        var reservados = accesorioRepository.obtenerCantidadReservadaPorAccesorios(ids, fechaInicio, fechaFin);

        var reservadoMap = reservados.stream().collect(Collectors.toMap(
                r -> (Long) r[0],
                r -> ((Number) r[1]).intValue()
        ));

        return activos.stream().map(a -> {
            int ya = reservadoMap.getOrDefault(a.getId(), 0);
            int stockDisp = Math.max(0, (a.getStock() == null ? 0 : a.getStock()) - ya);
            int max = Math.min(stockDisp, Math.max(0, numeroBicis));

            return AccesorioOpcionDTO.builder()
                    .id(a.getId())
                    .nombre(a.getNombre())
                    .precioDia(a.getPrecioDia() == null ? 0d : a.getPrecioDia().doubleValue())
                    .stockDisponible(stockDisp)
                    .maxPorReserva(max)
                    .build();
        }).toList();
    }



}
