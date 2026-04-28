package com.alquiler.alquilerbicicletas.servicios;

import com.alquiler.alquilerbicicletas.modelos.Reserva;
import com.alquiler.alquilerbicicletas.modelos.ReservaExtra;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FacturaService {

    public void generarFacturaConExtrasPendientes(Reserva reserva) {
        List<ReservaExtra> extrasPendientes = reserva.getExtras().stream()
                .filter(re -> !re.getPagado())
                .toList();

        // Lógica para generar factura con extrasPendientes + datos anteriores ya pagados
        // ...

        // Por ejemplo, generar PDF y enviar correo al cliente
    }

}
