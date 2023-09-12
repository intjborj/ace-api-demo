create table IF NOT EXISTS pms.managing_physicians
(
    id uuid not null primary key,

    employee  uuid
    constraint fk_attending_physician_employees
    references hrm.employees
    on update cascade on delete restrict,

    "case"  uuid
    constraint fk_attending_physician_cases
    references pms.cases
    on update cascade on delete restrict,

    is_ap boolean,

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted            boolean
);
