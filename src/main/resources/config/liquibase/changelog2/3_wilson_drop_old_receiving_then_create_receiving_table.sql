drop table if exists "inventory"."receiving_report_items";
drop table if exists "inventory"."receiving_report";
create table inventory.receiving_report
(
  id                            uuid not null primary key,
  received_type                 varchar,
  received_no                   varchar,
  received_date                 timestamp without time zone,
  purchase_order                uuid,
  received_ref_no               varchar,
  received_ref_date             timestamp without time zone,
  received_dep                  uuid not null,
  supplier                      uuid not null,
  payment_terms                 uuid not null,
  received_remarks              text,
  is_posted                     bool,
  is_void                       bool,



  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (purchase_order) REFERENCES  inventory.purchase_order (id),
  FOREIGN KEY (received_dep) REFERENCES  public.departments (id),
  FOREIGN KEY (supplier) REFERENCES  inventory.supplier (id),
  FOREIGN KEY (payment_terms) REFERENCES  inventory.payment_terms (id)
);