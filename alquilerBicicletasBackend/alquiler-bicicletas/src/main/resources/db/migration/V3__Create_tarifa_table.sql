CREATE TABLE IF NOT EXISTS alquiler_bicicletas.tarifa (
                                                          id SERIAL PRIMARY KEY,
                                                          nombre VARCHAR(50) NOT NULL,
                                                          descripcion VARCHAR(100) NOT NULL,
                                                          precio NUMERIC(10, 2) NOT NULL,
                                                          horas_incluidas INTEGER NOT NULL,
                                                          dias_minimos INTEGER NOT NULL,
                                                          precio_por_dia BOOLEAN NOT NULL,
                                                          activa BOOLEAN NOT NULL DEFAULT TRUE,
                                                          hora_inicio TIME,   -- Nueva columna para inicio del horario
                                                          hora_fin TIME       -- Nueva columna para fin del horario
);

