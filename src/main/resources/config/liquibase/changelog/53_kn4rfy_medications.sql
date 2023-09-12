create table "pms"."medications"
(
  id                 uuid not null
    constraint medications_pkey
      primary key,
  "case"             uuid
    constraint fk_medications_cases
      references pms.cases
      on update cascade on delete restrict,
  medicine           uuid,
  employee           uuid,
  frequency          varchar,
  dose               varchar,
  route              varchar,
  remarks            varchar,
  type               varchar,
  entry_datetime     timestamp without time zone default CURRENT_TIMESTAMP,
  created_by         varchar(50),
  created_date       timestamp(6)                default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6)                default CURRENT_TIMESTAMP
);