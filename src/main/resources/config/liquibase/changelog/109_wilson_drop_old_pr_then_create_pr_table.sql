drop table if exists "inventory"."purchase_request_items";
drop table if exists "inventory"."purchase_request";
create table inventory.purchase_request
(
  id                            uuid not null primary key,
  pr_no                         varchar,
  pr_date_requested             timestamp without time zone,
  pr_date_needed                timestamp without time zone,
  supplier                      uuid not null,
  user_id                       uuid not null,
  user_fullname                 varchar,
  requested_dep                 uuid not null,
  requesting_dep                uuid not null,
  pr_type                       varchar,
  is_approve                    bool,
  approver                      uuid,
  approver_fullname             varchar,
  is_po_create                  bool,
  status                        varchar,



  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (supplier) REFERENCES  inventory.supplier (id),
  FOREIGN KEY (requested_dep) REFERENCES  public.departments (id),
  FOREIGN KEY (requesting_dep) REFERENCES  public.departments (id)
);