CREATE SCHEMA IF NOT EXISTS ancillary;

create table IF NOT EXISTS public.department
(
  id                 uuid not null
    constraint department_pkey
      primary key,
  name               varchar,
  description        varchar,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
  deleted            boolean,
  special_area       boolean,
  parent_department  uuid,
  department_head    uuid,
  deprecated         boolean
);
