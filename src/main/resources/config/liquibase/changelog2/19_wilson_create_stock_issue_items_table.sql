drop table if exists "inventory"."stock_issue_items" CASCADE;
create table inventory.stock_issue_items
(
  id                            uuid not null primary key,
  stock_issue                   uuid,
  item                          uuid,
  issue_qty                     int,
  unit_cost                     numeric,
  is_posted                     bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (stock_issue) REFERENCES  inventory.stock_issue (id),
  FOREIGN KEY (item) REFERENCES  inventory.item (id)
);