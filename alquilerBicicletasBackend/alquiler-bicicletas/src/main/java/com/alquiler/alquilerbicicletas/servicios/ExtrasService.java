package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.enumerados.AplicableExtra;
import com.alquiler.alquilerbicicletas.enumerados.EstadoReserva;
import com.alquiler.alquilerbicicletas.modelos.Extra;
import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.alquiler.alquilerbicicletas.modelos.ReservaExtra;
import com.alquiler.alquilerbicicletas.repositorios.ExtraRepository;
import com.alquiler.alquilerbicicletas.repositorios.ReservaExtraRepository;
import com.alquiler.alquilerbicicletas.repositorios.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
@Service
@RequiredArgsConstructor
public class ExtrasService {

    private final ExtraRepository extraRepository;
    private final ReservaExtraRepository reservaExtraRepository;
    private final ReservaRepository reservaRepository;



    @Transactional(readOnly = true) // <- esto asegura que se cargue correctamente el proxy lazy
    public List<Extra> listar(AplicableExtra aplicable) {
        if (aplicable != null) {
            return extraRepository.findByAplicableA(aplicable);
        }
        return extraRepository.findAll();
    }


    // Listar extras aplicados a una reserva
    public List<ReservaExtra> listarDeReserva(Long reservaId) {
        return reservaExtraRepository.findByReservaId(reservaId);
    }

    // Agregar un extra a una reserva
    public Reserva agregarExtra(Long reservaId, Long extraId, int cantidad, AplicableExtra contexto) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));

        Extra extra = extraRepository.findById(extraId)
                .orElseThrow(() -> new IllegalArgumentException("Extra no encontrado"));

        // Crear nueva línea de reserva_extra
        ReservaExtra re = ReservaExtra.builder()
                .reserva(reserva)
                .extra(extra)
                .cantidad(cantidad)
                .precioTotal(extra.getPrecio() * cantidad)
                .pagado(false)
                .build();

        reservaExtraRepository.save(re);

        // Opcional: actualizar total de extras en la reserva (si lo tienes en entidad)
        //reserva.calcularTotalesExtras();

        return reserva;
    }

    // Eliminar un extra de una reserva
    public Reserva eliminarExtra(Long reservaId, Long extraId) {
        List<ReservaExtra> lista = reservaExtraRepository.findByReservaId(reservaId);
        lista.stream()
                .filter(re -> re.getExtra().getId().equals(extraId))
                .findFirst()
                .ifPresent(reservaExtraRepository::delete);

        return reservaRepository.findById(reservaId)
                .orElseThrow(() -> new IllegalArgumentException("Reserva no encontrada"));
    }
}
