create table inventory.signature_table
(
  id                             uuid not null primary key,
  department_id                  uuid,
  signature_type                 varchar,
  signature_header               varchar,
  signature_person               varchar,
  signature_position             varchar,
  is_current_user                bool,
  deleted_by                     varchar(50),
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);