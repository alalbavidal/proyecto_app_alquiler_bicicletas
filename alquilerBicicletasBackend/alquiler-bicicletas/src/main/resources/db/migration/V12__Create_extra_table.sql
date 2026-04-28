SET search_path TO alquiler_bicicletas;

CREATE TABLE alquiler_bicicletas.extra (
                                           id SERIAL PRIMARY KEY,
                                           nombre VARCHAR(50) NOT NULL,
                                           descripcion TEXT,
                                           precio DECIMAL(10, 2) NOT NULL,
                                           aplicable_a VARCHAR(20) NOT NULL CHECK (aplicable_a IN ('RESERVA', 'DEVOLUCION', 'AMBOS')),
                                           restricciones TEXT,
                                           fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);

-- Índices para mejorar el rendimiento
CREATE INDEX idx_extra_nombre ON alquiler_bicicletas.extra(nombre);
CREATE INDEX idx_extra_aplicable ON alquiler_bicicletas.extra(aplicable_a);

-- Comentarios para documentación
COMMENT ON TABLE alquiler_bicicletas.extra IS 'Tabla de servicios extras y suplementos para reservas';
COMMENT ON COLUMN alquiler_bicicletas.extra.nombre IS 'Nombre del servicio extra';
COMMENT ON COLUMN alquiler_bicicletas.extra.descripcion IS 'Descripción detallada del servicio';
COMMENT ON COLUMN alquiler_bicicletas.extra.precio IS 'Precio del servicio extra';
COMMENT ON COLUMN alquiler_bicicletas.extra.aplicable_a IS 'Cuando aplica: RESERVA, DEVOLUCION o AMBOS';
COMMENT ON COLUMN alquiler_bicicletas.extra.restricciones IS 'Restricciones del servicio';
COMMENT ON COLUMN alquiler_bicicletas.extra.fecha_creacion IS 'Fecha de creación del registro';

INSERT INTO extra (nombre, descripcion, precio, aplicable_a, restricciones) VALUES
                                                                                ('Suplemento recogida', 'Recogida fuera de horario', 8.00, 'RESERVA', 'No aplicable para 4+ bicis'),
                                                                                ('Asistencia técnica', 'In situ durante el alquiler', 30.00, 'RESERVA', 'Sujeto a disponibilidad'),
                                                                                ('Pérdida de llave', NULL, 15.00, 'DEVOLUCION', NULL),
                                                                                ('Entrega con retraso', 'Prórroga por periodo', 8.00, 'DEVOLUCION', 'A decisión del dependiente');