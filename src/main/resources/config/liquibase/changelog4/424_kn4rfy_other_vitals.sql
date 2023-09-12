create table pms.other_vital_signs
(
    id                 uuid not null
        constraint other_vital_signs_pkey
            primary key,
    entry_datetime     timestamp    default CURRENT_TIMESTAMP,
    crt                varchar(10),
    cbs                varchar(10),
    cgs                varchar(10),
    cbc                varchar(10),
    note               varchar,
    "case"             uuid
        constraint fk_other_vital_signs_cases
            references pms.cases
            on update cascade on delete restrict,
    employee           uuid,
    deleted            boolean,
    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);
