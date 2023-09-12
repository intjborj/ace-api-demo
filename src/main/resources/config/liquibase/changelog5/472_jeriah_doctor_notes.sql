create table pms.doctor_notes
(
  id                 uuid not null
    constraint doctor_notes_pkey
      primary key,
  entry_datetime     timestamp    default CURRENT_TIMESTAMP,
  subjective         varchar,
  objective          varchar,
  assessment         varchar,
  plan               varchar,
  employee           uuid,
  "case"             uuid
    constraint fk_doctor_notes_cases
      references pms.cases
      on update cascade on delete restrict,
  deleted            boolean,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);