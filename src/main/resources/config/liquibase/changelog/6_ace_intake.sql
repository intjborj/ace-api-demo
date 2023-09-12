create table pms.intakes
(
  id                 uuid not null
    constraint intake_pkey
      primary key,
  entry_datetime     timestamp    default CURRENT_TIMESTAMP,
  po_intake          varchar,
  tube_intake        varchar,
  ivf_intake         varchar,
  blood_intake       varchar,
  tpn_intake         varchar,
  pb_intake          varchar,
  medication_intake  varchar,
  "case"             uuid
    constraint fk_intakes_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);