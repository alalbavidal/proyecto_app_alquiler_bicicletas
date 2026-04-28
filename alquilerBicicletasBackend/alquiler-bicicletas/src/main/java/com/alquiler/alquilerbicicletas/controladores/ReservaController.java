package com.alquiler.alquilerbicicletas.controladores;


import com.alquiler.alquilerbicicletas.dto.*;
import com.alquiler.alquilerbicicletas.excepciones.AccesorioYaReservadoException;
import com.alquiler.alquilerbicicletas.excepciones.BicicletaYaReservadaException;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.alquiler.alquilerbicicletas.repositorios.ReservaRepository;
import com.alquiler.alquilerbicicletas.servicios.CancelarReservaService;
import com.alquiler.alquilerbicicletas.servicios.ContratoService;
import com.alquiler.alquilerbicicletas.servicios.EmailService;
import com.alquiler.alquilerbicicletas.servicios.ReservaService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.ui.Model;


import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.stream.Collectors;
@RestController
@RequestMapping("/api/reservas")
@RequiredArgsConstructor
public class ReservaController {

    @Value("${frontend.base-url}")
    private String frontendBaseUrl;

    private static final Logger logger = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;
    private final ReservaRepository reservaRepository;
    private final EmailService emailService;
    private final CancelarReservaService cancelarReservaService;
    private final ContratoService contratoService;

    // -----------------------------------
    // NECESARIOS
    // -----------------------------------

    // ------------------------------------------------------
    // Crear reserva con cliente (incluye envío de confirmación)
    // ------------------------------------------------------
    @PostMapping("/con-cliente")
    public ResponseEntity<?> crearReservaConCliente(
            @RequestBody ReservaConClienteDTO dto,
            @RequestParam(defaultValue = "es") String idioma) { // ← NUEVO parámetro

        // Validar idioma
        if (!"es".equals(idioma) && !"en".equals(idioma)) {
            idioma = "es"; // Por defecto español
        }

        if (dto.getReserva() == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "La reserva no puede ser nula."));
        }
        try {
            ReservaResponseDTO reserva = reservaService.crearReservaConCliente(dto, idioma); // ← Pasar idioma
            return new ResponseEntity<>(reserva, HttpStatus.CREATED);
        } catch (BicicletaYaReservadaException | AccesorioYaReservadoException e) {
            logger.warn("Error al crear la reserva con cliente: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            logger.error("Error inesperado al crear la reserva con cliente", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Mantener endpoint original para compatibilidad
    @PostMapping("/con-cliente-compatibilidad")
    public ResponseEntity<?> crearReservaConCliente(@RequestBody ReservaConClienteDTO dto) {
        return crearReservaConCliente(dto, "es");
    }

    // -------------------------
    // Obtener reserva por ID
    // -------------------------
    @GetMapping("/{id}")
    public ResponseEntity<?> obtenerReserva(@PathVariable Long id) {
        try {
            Reserva reserva = reservaService.obtenerReservaPorId(id);
            return ResponseEntity.ok(reserva);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error inesperado al consultar la reserva", e);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Reserva no encontrada.");
        }
    }

    @GetMapping(value = "/verReserva/{id}", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> verReservaPublica(@PathVariable Long id) {
        try {
            Reserva reserva = reservaService.obtenerReservaPorId(id);
            if (reserva == null) {
                return ResponseEntity.notFound().build();
            }

            // Generate the contract PDF and get the file path
            String pdfPath = String.valueOf(contratoService.generarContratoPDF(reserva));

            // Read the file into a byte array
            Path path = Paths.get(pdfPath);
            byte[] pdfContenido = Files.readAllBytes(path);

            return ResponseEntity.ok()
                    .header("Content-Disposition", "inline; filename=contrato_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .contentLength(pdfContenido.length)
                    .body(pdfContenido);
        } catch (IOException e) {
            logger.error("Error al leer el archivo PDF para la reserva con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        } catch (Exception e) {
            logger.error("Error al generar el PDF para la reserva con ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // ------------------------------
    // Cancelar reserva por token
    // ------------------------------
    @GetMapping("/cancelar/{token}")
    public ResponseEntity<?> cancelarReservaConToken(@PathVariable String token) {
        return cancelarReservaService.cancelarPorTokenYGenerarRespuesta(token);
    }

    // ... resto de tus métodos comentados permanecen igual
}