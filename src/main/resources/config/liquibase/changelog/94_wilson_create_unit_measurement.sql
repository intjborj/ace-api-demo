
create table inventory.unit_measurements
(
  id                            uuid not null primary key,
  unit_code                     varchar,
  unit_description              varchar,
  is_small                      bool,
  is_big                        bool,
  is_active                     bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool
);