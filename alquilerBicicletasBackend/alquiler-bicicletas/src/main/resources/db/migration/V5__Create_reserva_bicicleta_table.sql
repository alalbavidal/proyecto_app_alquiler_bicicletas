CREATE TABLE if not exists alquiler_bicicletas.reserva_bicicleta (
                                                                     bicicleta_id BIGINT NOT NULL,
                                                                     reserva_id BIGINT NOT NULL,
                                                                     PRIMARY KEY (bicicleta_id, reserva_id), -- Definición de la clave primaria compuesta
                                                                     FOREIGN KEY (bicicleta_id) REFERENCES bicicleta(id),
                                                                     FOREIGN KEY (reserva_id) REFERENCES reserva(id)
);