
create table inventory.physical_logs_count
(
  id                            uuid not null primary key,
  physical_count                uuid,
  log_user                      varchar,
  log_count                     int,



  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (physical_count) REFERENCES  inventory.physical_count (id)
);