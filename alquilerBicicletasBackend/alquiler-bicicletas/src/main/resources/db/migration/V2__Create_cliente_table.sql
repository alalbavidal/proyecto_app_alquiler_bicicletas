CREATE TABLE if not exists alquiler_bicicletas.cliente (
                                             id SERIAL PRIMARY KEY,
                                             nombre VARCHAR(100) NOT NULL,
                                             apellido VARCHAR(100) NOT NULL,
                                             fecha_nacimiento DATE NOT NULL,
                                             email VARCHAR(100) NOT NULL UNIQUE,
                                             telefono VARCHAR(20),
                                             documento_identidad VARCHAR(50),
                                             foto_documento_url VARCHAR(255),
                                             fecha_registro DATE
);