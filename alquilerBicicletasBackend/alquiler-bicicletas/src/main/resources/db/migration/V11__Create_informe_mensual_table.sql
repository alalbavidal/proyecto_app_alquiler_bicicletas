CREATE TABLE alquiler_bicicletas.informe_mensual (
                                                     id SERIAL PRIMARY KEY,
                                                     mes INTEGER NOT NULL,
                                                     anio INTEGER NOT NULL,
                                                     total_reservas INTEGER,
                                                     ingresos_totales DECIMAL(12,2),
                                                     archivo_url VARCHAR(255),
                                                     fecha_generacion TIMESTAMP NOT NULL
);