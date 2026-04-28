CREATE TABLE if not exists alquiler_bicicletas.accesorio (
                                               id SERIAL PRIMARY KEY,
                                               nombre VARCHAR(100) NOT NULL,
                                               descripcion TEXT,
                                               precio_dia DECIMAL(10,2) NOT NULL,
                                               stock INTEGER NOT NULL DEFAULT 0,
                                               observaciones VARCHAR(100),
                                               activo BOOLEAN NOT NULL DEFAULT true
);