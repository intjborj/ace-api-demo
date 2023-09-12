drop table if exists "inventory"."receiving_report_items";
create table inventory.receiving_report_items
(
  id                            uuid not null primary key,
  receiving_report              uuid,
  item                          uuid,
  ref_poItem                    uuid,
  rec_qty                       int,
  rec_unit_cost                 numeric,
  rec_disc_cost                 numeric,
  is_fg                         bool,
  is_discount                   bool,
  is_completed                  bool,
  is_partial                    bool,
  is_posted                     bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (receiving_report) REFERENCES  inventory.receiving_report (id),
  FOREIGN KEY (item) REFERENCES  inventory.item (id),
  FOREIGN KEY (ref_poItem) REFERENCES  inventory.purchase_order_items (id)
);