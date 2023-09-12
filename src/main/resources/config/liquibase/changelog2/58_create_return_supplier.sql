drop table if exists "inventory"."return_supplier_items";
drop table if exists "inventory"."return_supplier";
create table inventory.return_supplier
(
  id                            uuid not null primary key,
  rts_no                        varchar,
  return_date                   timestamp without time zone,
  receiving_report              uuid,
  received_ref_no               varchar,
  received_ref_date             timestamp without time zone,
  department                    uuid,
  supplier                      uuid,
  received_by                   varchar,
  is_posted                     bool,
  is_void                       bool,



  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (receiving_report) REFERENCES  inventory.receiving_report (id),
  FOREIGN KEY (department) REFERENCES  public.departments (id),
  FOREIGN KEY (supplier) REFERENCES  inventory.supplier (id)
);