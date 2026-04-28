package com.alquiler.alquilerbicicletas.enumerados;

public enum EstadoReserva {
    PENDIENTE,    // Reserva creada pero no confirmada
    CONFIRMADA,    // Reserva confirmada y pago realizado
    CANCELADA,     // Reserva cancelada por cliente o sistema
    COMPLETADA     // Bicicleta devuelta y alquiler finalizado
}