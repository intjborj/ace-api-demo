create table IF NOT EXISTS billing.price_tiers
(
    id                uuid not null primary key,
    title             varchar,
    description       varchar,
    created_by         varchar(50),
    created_date       timestamp(6) default now(),
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default now(),
    deleted            boolean
);

create table IF NOT EXISTS billing.price_tier_details
(
    id uuid not null primary key,
    price_tier  uuid
    constraint fk_price_tiers_config_price_tiers
    references billing.price_tiers
    on update cascade on delete restrict,

    registry_type varchar,
    accommodation_type varchar,
    room_type varchar,
    department varchar,

    from_datetime timestamp(6),
    to_datetime timestamp(6),

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted            boolean
);
