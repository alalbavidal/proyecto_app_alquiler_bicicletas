package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.modelos.Accesorio;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.alquiler.alquilerbicicletas.modelos.ReservaAccesorio;
import com.alquiler.alquilerbicicletas.repositorios.AccesorioRepository;
import com.alquiler.alquilerbicicletas.repositorios.ReservaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import java.util.Map;

@Service
public class CancelarReservaService {

    private static final Logger logger = LoggerFactory.getLogger(CancelarReservaService.class);

    @Autowired
    private ReservaRepository reservaRepository;

    @Autowired
    private AccesorioRepository accesorioRepository;

    public void cancelarPorToken(String token) {
        Reserva reserva = reservaRepository.findByCancelToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token no válido o reserva no encontrada"));
        cancelar(reserva);
    }

    public void cancelar(Reserva reserva) {
        if (!reserva.puedeCancelarse()) {
            throw new IllegalStateException("La reserva no puede cancelarse en su estado actual: " + reserva.getEstado());
        }

        for (ReservaAccesorio ra : reserva.getAccesorios()) {
            Accesorio accesorio = ra.getAccesorio();
            accesorio.setStock(accesorio.getStock() + ra.getCantidad());
            accesorioRepository.save(accesorio);
        }

        reserva.cancelar();
        reservaRepository.save(reserva);
    }

    public ResponseEntity<?> cancelarPorTokenYGenerarRespuesta(String token) {
        boolean esGet = RequestContextHolder.getRequestAttributes() != null &&
                ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes())
                        .getRequest().getMethod().equals("GET");
        try {
            cancelarPorToken(token);
            if (esGet) return ResponseEntity.ok().contentType(MediaType.TEXT_HTML).body(htmlExito());
            return ResponseEntity.ok().body(Map.of("mensaje", "Reserva cancelada correctamente."));
        } catch (IllegalArgumentException e) {
            logger.warn("Token de cancelación inválido o no encontrado: {}", e.getMessage());
            if (esGet) return ResponseEntity.status(HttpStatus.NOT_FOUND).contentType(MediaType.TEXT_HTML).body(htmlError("No se ha podido encontrar la reserva o el enlace ha expirado."));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "Reserva no encontrada o token inválido."));
        } catch (IllegalStateException e) {
            logger.warn("No se puede cancelar la reserva: {}", e.getMessage());
            if (esGet) return ResponseEntity.status(HttpStatus.BAD_REQUEST).contentType(MediaType.TEXT_HTML).body(htmlError("No se puede cancelar la reserva en este momento debido a su estado actual."));
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "No se puede cancelar la reserva en este estado."));
        } catch (Exception e) {
            logger.error("Error inesperado al cancelar la reserva", e);
            if (esGet) return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).contentType(MediaType.TEXT_HTML).body(htmlError("Ha ocurrido un error inesperado al intentar cancelar la reserva."));
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of("error", "Error interno del servidor. Intenta más tarde."));
        }
    }

    private String htmlExito() {
        return """
            <!DOCTYPE html>
            <html><head><meta charset="UTF-8"><title>Reserva Cancelada</title>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; }
                .success { color: #155724; background-color: #d4edda; border-color: #c3e6cb; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
                h1 { color: #2E86C1; }
                .btn { display: inline-block; background-color: #2E86C1; color: white; padding: 10px 15px; text-decoration: none; border-radius: 4px; }
            </style></head><body>
                <h1>Reserva Cancelada</h1>
                <div class="success"><p>Tu reserva ha sido cancelada correctamente.</p></div>
                <p>Gracias por usar nuestro servicio de alquiler de bicicletas.</p>
                <a href="https://www.bikerental.com.es/" class="btn">Volver a la página principal</a>
            </body></html>
        """;
    }

    private String htmlError(String mensaje) {
        return """
            <!DOCTYPE html>
            <html><head><meta charset="UTF-8"><title>Error al Cancelar Reserva</title>
            <style>
                body { font-family: Arial, sans-serif; line-height: 1.6; max-width: 600px; margin: 0 auto; padding: 20px; }
                .error { color: #721c24; background-color: #f8d7da; border-color: #f5c6cb; padding: 15px; border-radius: 4px; margin-bottom: 20px; }
                h1 { color: #2E86C1; }
                .btn { display: inline-block; background-color: #2E86C1; color: white; padding: 10px 15px; text-decoration: none; border-radius: 4px; }
            </style></head><body>
                <h1>Error al Cancelar Reserva</h1>
                <div class="error"><p>%s</p></div>
                <a href="https://www.bikerental.com.es/" class="btn">Volver a la página principal</a>
            </body></html>
        """.formatted(mensaje);
    }


    @Transactional
    public void cancelarPorId(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
        cancelar(reserva);
    }
}