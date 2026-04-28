CREATE TABLE if not exists alquiler_bicicletas.bicicleta (
                                               id SERIAL PRIMARY KEY,
                                               modelo VARCHAR(50) NOT NULL,
                                               numero VARCHAR(10) NOT NULL UNIQUE,
                                               bastidor VARCHAR(50) UNIQUE,
                                               candado VARCHAR(50),
                                               clave_candado VARCHAR(20),
                                               descripcion TEXT,
                                               imagen_url VARCHAR(255),
                                               estado VARCHAR(20) NOT NULL CHECK (estado IN ('ACTIVO', 'ALQUILADA','EN_REPARACION', 'BAJA')),
                                               fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL
);