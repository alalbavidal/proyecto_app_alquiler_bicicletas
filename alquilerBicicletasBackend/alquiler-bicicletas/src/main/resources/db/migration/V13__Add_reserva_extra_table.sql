-- Creación de la tabla reserva_extra
create table if not exists alquiler_bicicletas.reserva_extra (
                                                                 id              bigint not null primary key,
                                                                 reserva_id      bigint not null,
                                                                 extra_id        bigint not null,
                                                                 cantidad        integer not null,
                                                                 precio_total    double precision not null,
                                                                 fecha_adicion   timestamp not null default CURRENT_TIMESTAMP,
                                                                 pagado          boolean not null default false,

    -- Relaciones con las tablas existentes
                                                                 constraint fk_reserva
                                                                     foreign key (reserva_id)
                                                                         references alquiler_bicicletas.reserva(id)
                                                                         on delete cascade,

                                                                 constraint fk_extra
                                                                     foreign key (extra_id)
                                                                         references alquiler_bicicletas.extra(id)
                                                                         on delete cascade
);

-- Indices para optimizar las búsquedas por reserva y extra
create index if not exists idx_reserva_extra_reserva_id
    on reserva_extra (reserva_id);

create index if not exists idx_reserva_extra_extra_id
    on reserva_extra (extra_id);