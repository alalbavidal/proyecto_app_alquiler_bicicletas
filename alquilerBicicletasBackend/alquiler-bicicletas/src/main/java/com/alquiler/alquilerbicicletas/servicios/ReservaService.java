package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.dto.*;
import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.excepciones.AccesorioYaReservadoException;
import com.alquiler.alquilerbicicletas.excepciones.BicicletaYaReservadaException;
import com.alquiler.alquilerbicicletas.modelos.*;
import com.alquiler.alquilerbicicletas.repositorios.*;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ReservaService {

    private static final Logger logger = LoggerFactory.getLogger(ReservaService.class);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private BicicletaRepository bicicletaRepository;

    @Autowired
    private AccesorioRepository accesorioRepository;

    @Autowired
    private TarifaService tarifaService;

    @Autowired
    private BicicletaService bicicletaService;

    @Autowired
    private AccesorioService accesorioService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private ExtrasService extrasService;

    @Autowired
    private ContratoService contratoService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CancelarReservaService cancelarReservaService;





    /**
     * Crea una reserva con un cliente existente o crea uno nuevo si no existe.
     * Cuando el cliente aún no está registrado o se desea una UX fluida sin preregistro
     * Cliente desde frontend
     * @param dto DTO que contiene los datos de la reserva y del cliente.
     * @return ReservaResponseDTO con los detalles de la reserva creada.
     */

    @Transactional
    public ReservaResponseDTO crearReservaConCliente(ReservaConClienteDTO dto, String idioma) {
        ClienteDTO clienteDTO = dto.getCliente();
        logger.info("Buscando cliente por email: {}", clienteDTO.getEmail());

        Cliente cliente = clienteRepository.findByEmail(clienteDTO.getEmail());

        if (cliente == null) {
            logger.info("Cliente no existe, registrando nuevo cliente...");
            cliente = clienteService.mapearDtoAEntidad(clienteDTO);
            cliente = clienteService.crearCliente(cliente);
            logger.info("Cliente creado con ID: {}", cliente.getId());
        } else {
            logger.info("Cliente ya existe con ID: {}", cliente.getId());
        }

        // Asignar cliente a la reserva
        ReservaDTO reservaDTO = dto.getReserva();
        reservaDTO.setClienteId(cliente.getId());

        logger.info("Procediendo a crear la reserva con cliente en idioma: {}", idioma);

        return crearReservaConIdioma(reservaDTO, idioma); // ← CORREGIDO
    }

    // Método para compatibilidad
    public ReservaResponseDTO crearReservaConCliente(ReservaConClienteDTO dto) {
        return crearReservaConCliente(dto, "es");
    }



    private ReservaDetalleDTO convertirAReservaDTO(Reserva reserva) {
        List<ReservaBicicletaDTO> bicicletas = reserva.getBicicletas().stream()
                .map(rb -> new ReservaBicicletaDTO(
                        rb.getBicicleta().getId(),
                        rb.getBicicleta().getNombre() // <- aquí traes el nombre
                ))
                .toList();

        List<ReservaAccesorioDTO> accesorios = reserva.getAccesorios().stream() // CORREGIDO AQUÍ
                .map(extra -> new ReservaAccesorioDTO(extra.getAccesorio().getId(), extra.getCantidad()))
                .toList();

        return new ReservaDetalleDTO(
                reserva.getCliente().getId(),
                reserva.getTarifa().getId(),
                reserva.getFechaInicio(),
                reserva.getFechaFin(),
                reserva.getPrecioTotal(),
                reserva.getExtrasTotal(),
                reserva.getEstado().name(),
                reserva.getContratoUrl(),
                bicicletas,
                accesorios,
                reserva.getCancelToken()
        );
    }

    // Método ORIGINAL - renombrarlo para evitar conflicto
    public ReservaResponseDTO crearReserva(ReservaDTO dto) {
        return crearReservaConIdioma(dto, "es");
    }

    @Transactional
    public ReservaResponseDTO crearReservaConIdioma(ReservaDTO dto, String idioma) {
        try {

            // Validar idioma
            if (!"es".equals(idioma) && !"en".equals(idioma)) {
                logger.warn("Idioma '{}' no soportado, usando español por defecto", idioma);
                idioma = "es";
            }
            validarReservaDTO(dto);

            // Verificar disponibilidad de bicicletas
            List<BicicletaDTO> disponibles = bicicletaService.findBicicletasDisponibles(dto.getFechaInicio(), dto.getFechaFin());
            Set<Long> idsDisponibles = disponibles.stream()
                    .map(BicicletaDTO::getId)
                    .collect(Collectors.toSet());

            for (ReservaBicicletaDTO bicicletaSolicitada : dto.getBicicletas()) {
                if (!idsDisponibles.contains(bicicletaSolicitada.getBicicletaId())) {
                    logger.warn("Bicicleta con ID {} no está disponible entre {} y {}",
                            bicicletaSolicitada.getBicicletaId(), dto.getFechaInicio(), dto.getFechaFin());
                    throw new BicicletaYaReservadaException("La bicicleta con ID " +
                            bicicletaSolicitada.getBicicletaId() + " ya está reservada para ese período.");
                }
            }

            // Verificar disponibilidad de accesorios
            if (dto.getAccesorios() != null && !dto.getAccesorios().isEmpty()) {
                // nº de bicis de la reserva -> tope funcional "1 por bici"
                int numBicicletas = dto.getBicicletas().size();

                List<Long> accesorioIds = dto.getAccesorios().stream()
                        .map(ReservaAccesorioDTO::getAccesorioId)
                        .toList();

                List<Object[]> resultadosReservas = accesorioRepository.obtenerCantidadReservadaPorAccesorios(
                        accesorioIds,
                        dto.getFechaInicio(),
                        dto.getFechaFin()
                );

                Map<Long, Integer> cantidadesReservadas = resultadosReservas.stream()
                        .collect(Collectors.toMap(
                                r -> (Long) r[0],
                                r -> ((Number) r[1]).intValue()
                        ));

                for (ReservaAccesorioDTO accesorioDTO : dto.getAccesorios()) {
                    Long accesorioId = accesorioDTO.getAccesorioId();
                    int cantidadSolicitada = accesorioDTO.getCantidad();

                    Accesorio accesorio = accesorioRepository.findById(accesorioId)
                            .orElseThrow(() -> new IllegalArgumentException("Accesorio con ID " + accesorioId + " no encontrado"));

                    // 1) Regla funcional: máximo 1 por bicicleta
                    if (cantidadSolicitada > numBicicletas) {
                        throw new IllegalArgumentException(
                                "Máximo " + numBicicletas + " unidades de " + accesorio.getNombre() + " (una por bicicleta)."
                        );
                    }

                    // 2) Regla de stock (lo que ya tenías)
                    int cantidadReservada = cantidadesReservadas.getOrDefault(accesorioId, 0);
                    int disponible = Math.max(0, accesorio.getStock() - cantidadReservada);
                    if (cantidadSolicitada > disponible) {
                        throw new AccesorioYaReservadoException(
                                "No hay suficiente stock de " + accesorio.getNombre() +
                                        ". Solicitado: " + cantidadSolicitada + ", Disponible: " + disponible
                        );
                    }
                }
            }


            Cliente cliente = clienteRepository.findById(dto.getClienteId())
                    .orElseThrow(() -> new IllegalArgumentException("Cliente con ID " + dto.getClienteId() + " no encontrado"));

            Tarifa tarifa = tarifaService.validarTarifa(dto.getTarifaId());

            Reserva reserva = new Reserva();
            reserva.setCliente(cliente);
            reserva.setTarifa(tarifa);
            reserva.setFechaInicio(dto.getFechaInicio());
            reserva.setFechaFin(dto.getFechaFin());
            reserva.setEstado(EstadoReserva.CONFIRMADA);
            reserva.setCancelToken(UUID.randomUUID().toString());

            // Calcular precio base (tarifa * cantidad de bicicletas)
            BigDecimal tarifaBase = BigDecimal.valueOf(tarifa.getPrecio());
            int numBicicletas = dto.getBicicletas().size();
            BigDecimal precioBase = tarifaBase.multiply(BigDecimal.valueOf(numBicicletas));
            reserva.setPrecioTotal(precioBase); // total inicial sin accesorios
            reserva.setExtrasTotal(BigDecimal.ZERO);

            Reserva guardada = reservaRepository.save(reserva);

            // Bicicletas
            if (dto.getBicicletas() != null) {
                for (ReservaBicicletaDTO bicicletaDTO : dto.getBicicletas()) {
                    Bicicleta bicicleta = bicicletaRepository.findById(bicicletaDTO.getBicicletaId())
                            .orElseThrow(() -> new IllegalArgumentException("Bicicleta con ID " +
                                    bicicletaDTO.getBicicletaId() + " no encontrada"));

                    ReservaBicicleta rb = new ReservaBicicleta();
                    ReservaBicicletaId rbId = new ReservaBicicletaId();
                    rbId.setReservaId(guardada.getId());
                    rbId.setBicicletaId(bicicleta.getId());

                    rb.setId(rbId);
                    rb.setReserva(guardada);
                    rb.setBicicleta(bicicleta);

                    guardada.getBicicletas().add(rb);
                }
            }

            // Accesorios
            BigDecimal extrasTotal = BigDecimal.ZERO;
            if (dto.getAccesorios() != null) {
                for (ReservaAccesorioDTO accesorioDTO : dto.getAccesorios()) {
                    Accesorio accesorio = accesorioRepository.findById(accesorioDTO.getAccesorioId())
                            .orElseThrow(() -> new IllegalArgumentException("Accesorio con ID " +
                                    accesorioDTO.getAccesorioId() + " no encontrado"));

                    ReservaAccesorio ra = new ReservaAccesorio();
                    ReservaAccesorioId id = new ReservaAccesorioId(guardada.getId(), accesorio.getId());

                    ra.setId(id);
                    ra.setReserva(guardada);
                    ra.setAccesorio(accesorio);
                    ra.setCantidad(accesorioDTO.getCantidad());

                    long dias = ChronoUnit.DAYS.between(dto.getFechaInicio(), dto.getFechaFin()) + 1;
                    int cantidad = accesorioDTO.getCantidad(); // <- cantidad solicitada por el cliente

                    BigDecimal precioAccesorio = accesorio.getPrecioDia()
                            .multiply(BigDecimal.valueOf(cantidad))  // <- esta cantidad, NO el stock
                            .multiply(BigDecimal.valueOf(dias))
                            .setScale(2, RoundingMode.HALF_UP);



                    ra.setPrecioTotal(precioAccesorio);
                    extrasTotal = extrasTotal.add(precioAccesorio);

                    guardada.getAccesorios().add(ra);
                }
            }

            // Sumar extras al total y actualizar
            guardada.setExtrasTotal(extrasTotal);
            guardada.setPrecioTotal(precioBase.add(extrasTotal));

            guardada = reservaRepository.save(guardada);

            // CAMBIO IMPORTANTE: Generar contrato PDF en el idioma seleccionado
            ContratoGenerado contrato = contratoService.generarContratoPDF(guardada, idioma);

            guardada.setContratoUrl(contrato.urlPublica());
            reservaRepository.save(guardada);


            // Enviar email con el archivo real
            emailService.enviarConfirmacionReserva(guardada, contrato.rutaLocal());


            // Construir respuesta
            List<ReservaBicicletaDTO> bicicletasDTO = guardada.getBicicletas().stream()
                    .map(rb -> new ReservaBicicletaDTO(
                            rb.getBicicleta().getId(),
                            rb.getBicicleta().getNombre()
                    ))
                    .collect(Collectors.toList());

            List<ReservaAccesorioDTO> accesoriosDTO = guardada.getAccesorios().stream()
                    .map(ra -> new ReservaAccesorioDTO(
                            ra.getAccesorio().getId(),
                            ra.getCantidad(),
                            ra.getPrecioTotal(),
                            ra.getAccesorio().getNombre()
                    ))
                    .collect(Collectors.toList());

            ReservaResponseDTO response = new ReservaResponseDTO();
            response.setId(guardada.getId());
            response.setClienteId(guardada.getCliente().getId());
            response.setTarifaId(guardada.getTarifa().getId());
            response.setFechaInicio(guardada.getFechaInicio());
            response.setFechaFin(guardada.getFechaFin());
            response.setPrecioTotal(guardada.getPrecioTotal());
            response.setExtrasTotal(guardada.getExtrasTotal());
            response.setEstado(guardada.getEstado().name());
            response.setContratoUrl(guardada.getContratoUrl());
            response.setCancelToken(guardada.getCancelToken());
            response.setBicicletas(bicicletasDTO);
            response.setAccesorios(accesoriosDTO);

            logger.info("Reserva creada exitosamente con ID: {}", guardada.getId());
            return response;

        } catch (BicicletaYaReservadaException e) {
            logger.warn("Intento de reservar bicicleta no disponible: {}", e.getMessage());
            throw e;
        } catch (IllegalStateException | IllegalArgumentException iae) {
            logger.warn("Error esperado al crear la reserva: {}", iae.getMessage());
            throw iae;
        } catch (DataAccessException dae) {
            logger.error("Error al acceder a la base de datos", dae);
            throw dae;
        } catch (AccesorioYaReservadoException e) {
            logger.warn("Intento de reservar accesorio no disponible: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error inesperado al crear la reserva. Línea final.", e);
            throw new RuntimeException("Error inesperado al crear la reserva", e);
        }
    }



    private void validarReservaDTO(ReservaDTO dto) {
        if (dto.getClienteId() == null) {
            throw new IllegalArgumentException("El ID del cliente no puede ser nulo");
        }
        if (dto.getTarifaId() == null) {
            throw new IllegalArgumentException("El ID de la tarifa no puede ser nulo");
        }
        if (dto.getFechaInicio() == null || dto.getFechaFin() == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }
        if (dto.getFechaInicio().isAfter(dto.getFechaFin())) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }


        if (dto.getBicicletas() == null || dto.getBicicletas().isEmpty()) {
            throw new IllegalArgumentException("Debe incluir al menos una bicicleta en la reserva");
        }
        for (ReservaBicicletaDTO biciDTO : dto.getBicicletas()) {
            if (biciDTO.getBicicletaId() == null || biciDTO.getBicicletaId() <= 0) {
                throw new IllegalArgumentException("ID de bicicleta inválido: " + biciDTO.getBicicletaId());
            }
        }

        if (dto.getAccesorios() != null) {
            for (ReservaAccesorioDTO accDTO : dto.getAccesorios()) {
                if (accDTO.getAccesorioId() == null) {
                    throw new IllegalArgumentException("El ID del accesorio no puede ser nulo");
                }
            }
        }
    }


    public Reserva obtenerReservaPorId(Long id) {
        return reservaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Reserva con ID " + id + " no encontrada"));
    }






    private ReservaDetalleDTO obtenerReservaDTO(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new EntityNotFoundException("Reserva no encontrada"));

        return convertirAReservaDTO(reserva);
    }






    public void cancelarPorTokenYGenerarRespuesta(String token) {
        cancelarReservaService.cancelarPorToken(token);
    }

    public void confirmarReserva(Reserva reserva) {
        if (reserva.getEstado() == EstadoReserva.PENDIENTE) {
            reserva.setEstado(EstadoReserva.CONFIRMADA);
        } else {
            throw new IllegalStateException("No se puede confirmar una reserva que no está pendiente.");
        }
    }

    public void agregarExtra(Reserva reserva, Extra extra, Integer cantidad) {
        ReservaExtra reservaExtra = new ReservaExtra();
        reservaExtra.setReserva(reserva);
        reservaExtra.setExtra(extra);
        reservaExtra.setCantidad(cantidad);
        reservaExtra.setPrecioTotal(BigDecimal.valueOf(extra.getPrecio()).multiply(BigDecimal.valueOf(cantidad)).doubleValue());
        reserva.getExtras().add(reservaExtra);
    }

    public BigDecimal calcularTotalAccesorios(Reserva reserva) {
        return reserva.getAccesorios().stream()
                .map(ReservaAccesorio::getPrecioTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }








}


