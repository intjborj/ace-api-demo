create table pms.vital_signs
(
  id                 uuid not null
    constraint vital_signs_pkey
      primary key,
  entry_datetime     timestamp    default CURRENT_TIMESTAMP,
  systolic           varchar,
  diastolic          varchar,
  temperature        varchar,
  pulse_rate         varchar,
  respiratory_rate   varchar,
  oxygen_saturation  varchar,
  "case"             uuid
    constraint fk_vital_signs_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);