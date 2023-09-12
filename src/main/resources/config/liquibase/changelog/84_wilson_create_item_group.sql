
create table inventory.item_groups
(
  id                             uuid not null primary key,
  item_code                      varchar,
  item_description               varchar,
  is_active                      bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool
);