
create table inventory.item_categories
(
  id                             uuid not null primary key,
  fk_item_group                  uuid not null,
  category_code                  varchar,
  category_description           varchar,
  is_active                      bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  foreign key (fk_item_group) references inventory.item_groups (id)
);