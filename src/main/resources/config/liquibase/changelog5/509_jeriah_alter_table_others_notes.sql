create table pms.physical_therapy_notes
    (
      id                 uuid not null
        constraint physical_therapy_notes_pkey
          primary key,
      entry_datetime     timestamp    default CURRENT_TIMESTAMP,
      subjective         varchar,
      objective          varchar,
      assessment         varchar,
      plan               varchar,
      employee           uuid,
      "case"             uuid
        constraint fk_physical_therapy_notes_cases
          references pms.cases
          on update cascade on delete restrict,
      deleted            boolean,
      created_by         varchar(50),
      created_date       timestamp(6) default CURRENT_TIMESTAMP,
      last_modified_by   varchar(50),
      last_modified_date timestamp(6) default CURRENT_TIMESTAMP
    );

create table pms.occupational_therapy_notes
(
  id                 uuid not null
    constraint occupational_therapy_notes_pkey
      primary key,
  entry_datetime     timestamp    default CURRENT_TIMESTAMP,
  subjective         varchar,
  objective          varchar,
  assessment         varchar,
  plan               varchar,
  employee           uuid,
  "case"             uuid
    constraint fk_occupational_therapy_notes_cases
      references pms.cases
      on update cascade on delete restrict,
  deleted            boolean,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);

create table pms.speech_therapy_notes
(
  id                 uuid not null
    constraint speech_therapy_notes_pkey
      primary key,
  entry_datetime     timestamp    default CURRENT_TIMESTAMP,
  subjective         varchar,
  objective          varchar,
  assessment         varchar,
  plan               varchar,
  employee           uuid,
  "case"             uuid
    constraint fk_speech_therapy_notes_cases
      references pms.cases
      on update cascade on delete restrict,
  deleted            boolean,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);