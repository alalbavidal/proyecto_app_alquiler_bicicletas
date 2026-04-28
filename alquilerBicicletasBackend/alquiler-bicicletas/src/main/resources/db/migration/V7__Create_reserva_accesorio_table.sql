CREATE TABLE IF NOT EXISTS alquiler_bicicletas.reserva_accesorio (
                                                                     reserva_id INTEGER NOT NULL REFERENCES alquiler_bicicletas.reserva(id) ON DELETE CASCADE,
                                                                     accesorio_id INTEGER NOT NULL REFERENCES alquiler_bicicletas.accesorio(id) ON DELETE RESTRICT,
                                                                     cantidad INTEGER NOT NULL DEFAULT 1 CHECK (cantidad > 0),
                                                                     precio_total DECIMAL(10,2) NOT NULL,
                                                                     PRIMARY KEY (reserva_id, accesorio_id)
);

CREATE INDEX if not exists idx_reserva_accesorio_reserva ON alquiler_bicicletas.reserva_accesorio(reserva_id);
CREATE INDEX if not exists idx_reserva_accesorio_accesorio ON alquiler_bicicletas.reserva_accesorio(accesorio_id);
