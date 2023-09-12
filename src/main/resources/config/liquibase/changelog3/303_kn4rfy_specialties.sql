create table "hospital_configuration"."specialties"
(
  id                 uuid not null
    constraint specialties_pkey
      primary key,
  name               varchar(50),
  description        varchar(300),
  deleted            bool,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);
