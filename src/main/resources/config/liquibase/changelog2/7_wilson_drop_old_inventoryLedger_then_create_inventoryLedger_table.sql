DELETE FROM inventory.document_types WHERE id='57a37685-a20c-415e-9837-e2f138fef667';
drop table if exists "inventory"."inventory_ledger" CASCADE;
create table inventory.inventory_ledger
(
  id                            uuid not null primary key,
  source_dep                    uuid,
  destination_dep               uuid,
  document_types                uuid,
  item                          uuid,
  reference_no                  varchar,
  ledger_date                   timestamp(10) default CURRENT_TIMESTAMP,
  ledger_qty_in                 int,
  ledger_qty_out                int,
  ledger_physical               int,
  ledger_unit_cost              numeric,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (source_dep) REFERENCES  public.departments (id),
  FOREIGN KEY (destination_dep) REFERENCES  public.departments (id),
  FOREIGN KEY (document_types) REFERENCES  inventory.document_types (id),
  FOREIGN KEY (item) REFERENCES  inventory.item (id)
);