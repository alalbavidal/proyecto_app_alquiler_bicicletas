-- db/migration/V9__Create_chequeo_devolucion_table.sql

-- Asegúrate de que la tabla empleado existe
CREATE TABLE IF NOT EXISTS alquiler_bicicletas.empleado (
                                                            id SERIAL PRIMARY KEY,
                                                            nombre VARCHAR(255) NOT NULL,
    puesto VARCHAR(255) NOT NULL
    );

-- Crea la tabla chequeo_devolucion
CREATE TABLE alquiler_bicicletas.chequeo_devolucion (
                                                        id SERIAL PRIMARY KEY,
                                                        empleado_id INT NOT NULL,
                                                        fecha TIMESTAMP NOT NULL,
                                                        observaciones TEXT,
                                                        FOREIGN KEY (empleado_id) REFERENCES alquiler_bicicletas.empleado(id)
);