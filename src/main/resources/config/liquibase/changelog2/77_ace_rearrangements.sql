drop table if exists "libraries"."quality_management_types" cascade;
drop schema if exists "libraries";

create table if not exists "referential"."quality_management_types"
(
  id                             uuid not null primary key,
  title                          varchar,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        boolean
);

alter table "referential"."icd_categories" rename to doh_icd_categories;
alter table "referential"."icd_codes" rename to doh_icd_codes;