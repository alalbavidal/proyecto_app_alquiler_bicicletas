-- Crear la tabla reserva si no existe (debería existir ya)
CREATE TABLE IF NOT EXISTS alquiler_bicicletas.reserva (
                                                           id SERIAL PRIMARY KEY,
                                                           cliente_id INTEGER NOT NULL REFERENCES alquiler_bicicletas.cliente(id) ON DELETE RESTRICT,
                                                           tarifa_id INTEGER NOT NULL REFERENCES alquiler_bicicletas.tarifa(id) ON DELETE RESTRICT,
                                                           fecha_inicio TIMESTAMP NOT NULL,
                                                           fecha_fin TIMESTAMP NOT NULL,
                                                           precio_total DECIMAL(10,2) NOT NULL,
                                                           extras_total DECIMAL(10,2),

                                                           estado VARCHAR(20) NOT NULL DEFAULT 'PENDIENTE'
                                                               CHECK (estado IN ('PENDIENTE', 'CONFIRMADA', 'CANCELADA', 'COMPLETADA')),
                                                           contrato_url VARCHAR(300),
                                                           cancel_token VARCHAR(100) UNIQUE,
                                                           fecha_creacion TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                                           fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- Restricciones adicionales
                                                           CONSTRAINT chk_fechas_validas CHECK (fecha_fin > fecha_inicio),
                                                           CONSTRAINT chk_precio_positivo CHECK (precio_total > 0)
);

-- Si la tabla ya existía, asegurarse de que tenga la columna fecha_actualizacion
ALTER TABLE alquiler_bicicletas.reserva
    ADD COLUMN IF NOT EXISTS fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP;



-- Eliminar columna bicicleta_id si existe (por ejemplo, si es obsoleta)
ALTER TABLE alquiler_bicicletas.reserva DROP COLUMN IF EXISTS bicicleta_id;

-- Crear índices necesarios (cada uno con IF NOT EXISTS)
CREATE INDEX IF NOT EXISTS idx_reserva_cliente ON alquiler_bicicletas.reserva(cliente_id);
CREATE INDEX IF NOT EXISTS idx_reserva_fechas ON alquiler_bicicletas.reserva(fecha_inicio, fecha_fin);
CREATE INDEX IF NOT EXISTS idx_reserva_estado ON alquiler_bicicletas.reserva(estado);
CREATE INDEX IF NOT EXISTS idx_reserva_actualizacion ON alquiler_bicicletas.reserva(fecha_actualizacion);

-- Crear o reemplazar la función para el trigger
CREATE OR REPLACE FUNCTION actualizar_timestamp()
    RETURNS TRIGGER AS $$
BEGIN
    NEW.fecha_actualizacion = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Eliminar el trigger si ya existe
DROP TRIGGER IF EXISTS trigger_actualizar_reserva ON alquiler_bicicletas.reserva;

-- Crear el trigger
CREATE TRIGGER trigger_actualizar_reserva
    BEFORE UPDATE ON alquiler_bicicletas.reserva
    FOR EACH ROW
EXECUTE FUNCTION actualizar_timestamp();

