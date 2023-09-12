create table pms.discharge_clearances
(
  id                 uuid not null
    constraint discharge_clearances_pkey
      primary key,
  is_cleared         bool,
  clearing_staff     uuid,
  department         uuid,
  "case"             uuid
    constraint fk_discharge_clearances_cases
      references pms.cases
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);
