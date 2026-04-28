package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.dto.BicicletaDisponibleDTO;
import com.alquiler.alquilerbicicletas.excepciones.BicicletaNotFoundException;
import com.alquiler.alquilerbicicletas.excepciones.DuplicateBicicletaException;
import com.alquiler.alquilerbicicletas.modelos.Bicicleta;
import com.alquiler.alquilerbicicletas.enumerados.EstadoBicicleta;
import com.alquiler.alquilerbicicletas.dto.BicicletaDTO;
import com.alquiler.alquilerbicicletas.modelos.Tarifa;
import com.alquiler.alquilerbicicletas.repositorios.BicicletaRepository;
import com.alquiler.alquilerbicicletas.repositorios.ReservaRepository;
import com.alquiler.alquilerbicicletas.repositorios.TarifaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.alquiler.alquilerbicicletas.util.RangoFechas;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class BicicletaService {

    private final BicicletaRepository bicicletaRepository;
    private final TarifaService tarifaService;



    @Autowired
    public BicicletaService(BicicletaRepository bicicletaRepository, TarifaService tarifaService, ReservaRepository reservaRepository, TarifaRepository tarifaRepository) {
        this.bicicletaRepository = bicicletaRepository;
        this.tarifaService = tarifaService;
    }

    @Transactional(readOnly = true)
    public List<BicicletaDTO> findBicicletasDisponibles(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return bicicletaRepository.findBicicletasDisponibles(fechaInicio, fechaFin).stream()
                .map(BicicletaDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<BicicletaDTO> findAllBicicletas() {
        return bicicletaRepository.findAll().stream()
                .map(BicicletaDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BicicletaDTO findBicicletaById(Long id) {
        return bicicletaRepository.findById(id)
                .map(BicicletaDTO::new)
                .orElseThrow(() -> new BicicletaNotFoundException(id));
    }

    @Transactional
    public BicicletaDTO saveBicicleta(BicicletaDTO bicicletaDTO) {
        validarUnicidad(bicicletaDTO, null);
        validarDatosBicicleta(bicicletaDTO);

        Bicicleta bicicleta = convertirDtoAEntidad(bicicletaDTO);
        bicicleta.setFechaCreacion(LocalDateTime.now());

        return new BicicletaDTO(bicicletaRepository.save(bicicleta));
    }

    @Transactional
    public BicicletaDTO updateBicicleta(Long id, BicicletaDTO bicicletaDTO) {
        Bicicleta bicicletaExistente = bicicletaRepository.findById(id)
                .orElseThrow(() -> new BicicletaNotFoundException(id));

        validarUnicidad(bicicletaDTO, bicicletaExistente);

        actualizarEntidadDesdeDTO(bicicletaExistente, bicicletaDTO);

        return new BicicletaDTO(bicicletaRepository.save(bicicletaExistente));
    }

    @Transactional
    public void deleteBicicleta1(Long id) {
        Bicicleta bicicleta = bicicletaRepository.findById(id)
                .orElseThrow(() -> new BicicletaNotFoundException(id));

        if (bicicleta.getEstado() == EstadoBicicleta.BAJA) {
            throw new IllegalStateException("La bicicleta ya está dada de baja");
        }

        bicicleta.setEstado(EstadoBicicleta.BAJA);
        bicicletaRepository.save(bicicleta);
    }


    //Método para borrar una bicicleta de la base de datos
   @Transactional
   public Bicicleta deleteBicicleta(Long id) {
       Bicicleta bicicleta = bicicletaRepository.findById(id)
               .orElseThrow(() -> new BicicletaNotFoundException(id));

       bicicletaRepository.delete(bicicleta);
       return bicicleta;
   }

    // Método optimizado para dar de baja una bicicleta sin borrar de la bbdd
//    @Transactional
//    public Bicicleta deleteBicicleta(Long id) {
//        Bicicleta bicicleta = bicicletaRepository.findById(id)
//                .orElseThrow(() -> new BicicletaNotFoundException(id));
//
//        if (bicicleta.getEstado() == EstadoBicicleta.BAJA) {
//            throw new IllegalStateException("La bicicleta ya está dada de baja");
//        }
//
//        // Opción 1: Usando el método save (activa los callbacks @PreUpdate)
//        bicicleta.setEstado(EstadoBicicleta.BAJA);
//        return bicicletaRepository.save(bicicleta);
//
//        // Opción 2: Usando la consulta optimizada
//        // int updated = bicicletaRepository.softDeleteById(id);
//        // if (updated == 0) {
//        //     throw new IllegalStateException("No se pudo dar de baja la bicicleta");
//        // }
//        // return bicicletaRepository.findById(id).get();
//    }



    // BicicletaService.java
    @Transactional(readOnly = true)
    public List<BicicletaDisponibleDTO> findBicicletasDisponiblesPorFechaYTarifa(LocalDate fecha, Long tarifaId) {
        Tarifa tarifa = tarifaService.validarTarifa(tarifaId);

        // 🔧 Unificar la lógica de rango aquí
        RangoFechas rango = tarifaService.calcularRangoFechas(fecha, tarifa);
        LocalDateTime fechaInicio = rango.getFechaInicio();
        LocalDateTime fechaFin = rango.getFechaFin();

        List<Bicicleta> libres = bicicletaRepository.findBicicletasDisponibles(fechaInicio, fechaFin);

        List<BicicletaDisponibleDTO> out = new ArrayList<>();
        for (Bicicleta bici : libres) {
            out.add(BicicletaDisponibleDTO.builder()
                    .bicicleta(new BicicletaDTO(bici))
                    .precioTarifa(tarifa.getPrecio())
                    .horarioTarifa(formatearHorario(tarifa, fechaInicio, fechaFin))
                    .fechaInicio(fechaInicio)
                    .fechaFin(fechaFin)
                    .build());
        }
        return out;
    }

    private String formatearHorario(Tarifa tarifa, LocalDateTime ini, LocalDateTime fin) {
        // Texto bonito para el front/email (ejemplos)
        if (Boolean.TRUE.equals(tarifa.isPrecioPorDia())) {
            // "Del 30/09/2025 09:00 al 07/10/2025 09:00"
            return String.format("Del %1$td/%1$tm/%1$tY %1$tH:%1$tM al %2$td/%2$tm/%2$tY %2$tH:%2$tM", ini, fin);
        }
        // Por horas
        return String.format("%1$tH:%1$tM - %2$tH:%2$tM", ini, fin);
    }


    /** Muestra horario legible según tipo de tarifa */
    private String construirEtiquetaHorario(Tarifa tarifa, RangoFechas rango) {
        if (Boolean.TRUE.equals(tarifa.isPrecioPorDia())) {
            Integer dias = tarifa.getDiasMinimos() != null ? tarifa.getDiasMinimos() : 1;
            return dias == 1 ? "Día completo" : dias + " días";
        }
        DateTimeFormatter f = DateTimeFormatter.ofPattern("HH:mm");
        var hi = tarifa.getHoraInicio();
        var hf = tarifa.getHoraFin();

        if (hi != null && hf != null) {
            String etiqueta = hi.format(f) + " - " + hf.format(f);
            // Si fin no es posterior a inicio, cruza a día siguiente (24h o franja pasada medianoche)
            if (!hf.isAfter(hi)) etiqueta += " (+1 día)";
            return etiqueta;
        }
        // Fallback a horasIncluidas si no hay horas fijas
        int horas = tarifa.getHorasIncluidas() != null ? tarifa.getHorasIncluidas() : 0;
        return horas > 0 ? horas + " h" : "Franja horaria";
    }



    @Transactional(readOnly = true)
    public List<BicicletaDTO> findByEstado(EstadoBicicleta estado) {
        return bicicletaRepository.findByEstado(estado).stream()
                .map(BicicletaDTO::new)
                .collect(Collectors.toList());
    }


    @Transactional(readOnly = true)
    public List<BicicletaDTO> findByModelo(String modelo) {
        return bicicletaRepository.findByModeloContainingIgnoreCase(modelo).stream()
                .map(BicicletaDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Optional<BicicletaDTO> findByNumeroBastidor(String bastidor) {
        return bicicletaRepository.findByBastidor(bastidor)
                .map(BicicletaDTO::new);
    }

    @Transactional(readOnly = true)
    public Optional<BicicletaDTO> findByNumero(String numero) {
        return bicicletaRepository.findByNumero(numero)
                .map(BicicletaDTO::new);
    }

    // Convierte la entidad Bicicleta a DTO incluyendo el precio calculado
    private BicicletaDTO convertirADTOConPrecio(Bicicleta bicicleta) {
        BicicletaDTO dto = new BicicletaDTO(bicicleta);
        // Aquí podrías añadir lógica para calcular precios especiales si es necesario
        return dto;
    }


    // Métodos auxiliares privados
    private void validarUnicidad(BicicletaDTO dto, Bicicleta bicicletaExistente) {
        // Validar número único
        if (bicicletaExistente == null || !bicicletaExistente.getNumero().equals(dto.getNumero())) {
            if (bicicletaRepository.existsByNumero(dto.getNumero())) {
                throw new DuplicateBicicletaException("número", dto.getNumero());
            }
        }

        // Validar bastidor único (si se proporciona)
        if (dto.getBastidor() != null &&
                (bicicletaExistente == null ||
                        !dto.getBastidor().equals(bicicletaExistente.getBastidor()))) {
            if (bicicletaRepository.existsByBastidor(dto.getBastidor())) {
                throw new DuplicateBicicletaException("bastidor", dto.getBastidor());
            }
        }
    }

    private Bicicleta convertirDtoAEntidad(BicicletaDTO dto) {
        return Bicicleta.builder()
                .modelo(dto.getModelo())
                .numero(dto.getNumero())
                .bastidor(dto.getBastidor())
                .candado(dto.getCandado())
                .claveCandado(dto.getClaveCandado())
                .descripcion(dto.getDescripcion())
                .imagenUrl(dto.getImagenUrl())
                .estado(dto.getEstado() != null ? dto.getEstado() : EstadoBicicleta.ACTIVO)
                .observaciones(dto.getObservaciones())

                .build();
    }

    private void actualizarEntidadDesdeDTO(Bicicleta entidad, BicicletaDTO dto) {
        entidad.setModelo(dto.getModelo());
        entidad.setNumero(dto.getNumero());
        entidad.setBastidor(dto.getBastidor());
        entidad.setCandado(dto.getCandado());
        entidad.setClaveCandado(dto.getClaveCandado());
        entidad.setDescripcion(dto.getDescripcion());
        entidad.setImagenUrl(dto.getImagenUrl());
        entidad.setEstado(dto.getEstado());
        entidad.setObservaciones(dto.getObservaciones());
    }

    private void validarDatosBicicleta(BicicletaDTO dto) {
        if (dto.getModelo() == null || dto.getModelo().trim().isEmpty()) {
            throw new IllegalArgumentException("El modelo de la bicicleta es obligatorio");
        }
        if (dto.getNumero() == null || dto.getNumero().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de la bicicleta es obligatorio");
        }

        if (dto.getBastidor() == null || dto.getBastidor().trim().isEmpty()) {
            throw new IllegalArgumentException("El número de bastidor de la bicicleta es obligatorio");
        }
        if (dto.getCandado() == null || dto.getCandado().trim().isEmpty()) {
            throw new IllegalArgumentException("El candado de la bicicleta es obligatorio");
        }
        if (dto.getClaveCandado() == null || dto.getClaveCandado().trim().isEmpty()) {
            throw new IllegalArgumentException("La clave del candado de la bicicleta es obligatoria");
        }
        if (dto.getDescripcion() == null || dto.getDescripcion().trim().isEmpty()) {
            throw new IllegalArgumentException("La descripción de la bicicleta es obligatoria");
        }

        if (dto.getEstado() == null) {
            throw new IllegalArgumentException("El estado de la bicicleta es obligatorio");
        }



    }

    @Transactional
    public BicicletaDTO actualizarImagenUrl(Long id, String url) {
        Bicicleta b = bicicletaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bicicleta no encontrada: " + id));
        b.setImagenUrl(url);
        return new BicicletaDTO(bicicletaRepository.save(b));
    }








}