create schema libraries;

create table libraries.quality_management_types
(
  id                             uuid not null primary key,
  title                          varchar,

  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        boolean
);