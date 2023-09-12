drop table if exists "inventory"."return_supplier_items";
create table inventory.return_supplier_items
(
  id                            uuid not null primary key,
  return_supplier               uuid,
  item                          uuid,
  receiving_item                uuid,
  return_qty                    int,
  return_unit_cost              numeric,
  return_remarks                text,
  is_posted                     bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (return_supplier) REFERENCES  inventory.return_supplier (id),
  FOREIGN KEY (item) REFERENCES  inventory.item (id),
  FOREIGN KEY (receiving_item) REFERENCES  inventory.receiving_report_items (id)
);