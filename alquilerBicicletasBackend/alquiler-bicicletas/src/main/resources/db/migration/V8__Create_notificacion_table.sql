CREATE TABLE alquiler_bicicletas.notificacion (
                                                  id SERIAL PRIMARY KEY,
                                                  reserva_id INTEGER NOT NULL REFERENCES alquiler_bicicletas.reserva(id),
                                                  tipo VARCHAR(50) NOT NULL CHECK (tipo IN ('confirmacion', 'recordatorio', 'cancelacion')),
                                                  mensaje TEXT,
                                                  fecha_envio TIMESTAMP NOT NULL,
                                                  estado VARCHAR(20) NOT NULL CHECK (estado IN ('pendiente', 'enviado', 'fallido'))
);