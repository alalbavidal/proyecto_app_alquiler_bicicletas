CREATE TABLE if not exists alquiler_bicicletas.empleado (
                                              id SERIAL PRIMARY KEY,
                                              nombre VARCHAR(100) NOT NULL,
                                              email VARCHAR(100) NOT NULL UNIQUE,
                                              rol VARCHAR(50) NOT NULL CHECK (rol IN ('admin', 'empleado'))
);