drop table if exists "inventory"."beginning_balance" CASCADE;
create table inventory.beginning_balance
(
  id                            uuid not null primary key,
  ref_num                       varchar,
  date_trans                    timestamp(6) default CURRENT_TIMESTAMP,
  item                          uuid,
  department                    uuid,
  quantity                      int,
  is_posted                     bool,
  is_cancel                     bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (item) REFERENCES  inventory.item (id),
  FOREIGN KEY (department) REFERENCES  public.departments (id)
);