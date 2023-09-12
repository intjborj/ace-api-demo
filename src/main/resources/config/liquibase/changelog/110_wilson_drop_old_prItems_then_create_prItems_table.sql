drop table if exists "inventory"."purchase_request_items";
create table inventory.purchase_request_items
(
  id                            uuid not null primary key,
  purchase_request              uuid,
  item                          uuid,
  ref_po                        uuid,
  ref_supitem_id                uuid,
  unit_cost                     numeric,
  requested_qty                 int,
  total                         numeric,
  deals                         varchar,
  last_unit_price               numeric,
  on_hand_qty                   int,
  remarks                       varchar,



  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (purchase_request) REFERENCES  inventory.purchase_request (id),
  FOREIGN KEY (item) REFERENCES  inventory.item (id)
);