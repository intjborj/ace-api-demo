create table pms.nurse_notes
(
  id                 uuid not null
    constraint nurse_notes_pkey
      primary key,
  entry_datetime     timestamp    default CURRENT_TIMESTAMP,
  focus              varchar,
  data               varchar,
  action             varchar,
  response           varchar,
  note               varchar,
  "case"             uuid
    constraint fk_nurse_notes_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);