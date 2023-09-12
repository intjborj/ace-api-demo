create table hospital_configuration.default_admission_items
(
  id                             uuid not null primary key,
  inventory_id                   uuid,
  item_id                        uuid,
  department_id                  uuid,
  quantity                       numeric,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
