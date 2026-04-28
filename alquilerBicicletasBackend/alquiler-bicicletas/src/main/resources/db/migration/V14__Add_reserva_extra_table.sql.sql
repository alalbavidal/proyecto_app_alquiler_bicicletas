CREATE SEQUENCE IF NOT EXISTS alquiler_bicicletas.reserva_extra_id_seq
    START 1
    INCREMENT 1
    OWNED BY alquiler_bicicletas.reserva_extra.id;

ALTER TABLE alquiler_bicicletas.reserva_extra
    ALTER COLUMN id SET DEFAULT nextval('alquiler_bicicletas.reserva_extra_id_seq');

SELECT setval(
               'alquiler_bicicletas.reserva_extra_id_seq',
               COALESCE((SELECT MAX(id) FROM alquiler_bicicletas.reserva_extra), 0) + 1,
               false
       );
