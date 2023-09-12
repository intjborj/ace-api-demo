drop table if exists "inventory"."department_stock_request_items";
create table inventory.department_stock_request_items
(
  id                                uuid not null primary key,
  department_stock_request          uuid,
  item                              uuid,
  quantity_requested                int,
  unit_cost                         numeric,
  prepared_qty                      int,
  is_posted                         bool,
  is_rejected                       bool,
  remarks                           text,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (department_stock_request) REFERENCES  inventory.department_stock_request (id),
  FOREIGN KEY (item) REFERENCES  inventory.item (id)
);