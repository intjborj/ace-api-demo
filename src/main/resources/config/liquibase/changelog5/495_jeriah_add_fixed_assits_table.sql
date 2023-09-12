
create schema "fixed_assets";

create table "fixed_assets"."fixed_assets"(
    id   uuid not null primary key,
    ssr  uuid null,
              CONSTRAINT ssr_fk FOREIGN KEY (ssr) REFERENCES inventory.receiving_report(id)
              ON DELETE SET NULL
              ON UPDATE CASCADE,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean
);


