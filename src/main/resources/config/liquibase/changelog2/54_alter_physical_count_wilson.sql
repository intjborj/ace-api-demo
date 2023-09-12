drop table if exists "inventory"."physical_count" CASCADE;
create table inventory.physical_count
(
  id                            uuid not null primary key,
  ref_no                        varchar,
  date_trans                    date,
  item                          uuid,
  department                    uuid,
  on_hand                       int,
  quantity                      int,
  variance                      int,
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