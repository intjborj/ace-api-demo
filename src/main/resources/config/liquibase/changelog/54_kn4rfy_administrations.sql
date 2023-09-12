create table "pms"."administrations"
(
  id                 uuid not null
    constraint administrations_pkey
      primary key,
  medication         uuid
    constraint fk_administrations_medications
      references pms.medications
      on update cascade on delete restrict,
  action             varchar,
  dose               varchar,
  remarks            varchar,
  entry_datetime     timestamp without time zone default CURRENT_TIMESTAMP,
  created_by         varchar(50),
  created_date       timestamp(6)                default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6)                default CURRENT_TIMESTAMP
);