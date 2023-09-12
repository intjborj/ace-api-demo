drop table if exists "inventory"."department_stock_request_items";
drop table if exists "inventory"."department_stock_request" CASCADE;
create table inventory.department_stock_request
(
  id                                uuid not null primary key,
  request_no                        varchar,
  requesting_department             uuid,
  issuing_department                uuid,
  requested_by                      uuid,
  prepared_by                       uuid,
  dispensed_by                      uuid,
  claimed_by                        uuid,
  request_type                      varchar,
  tag                               varchar,
  purpose                           text,
  remarks                           text,
  is_canceled                        bool,
  status                            int, -- 0 => new ; 1 => pending; 2 => canceled; 3 => claimable; 4 => claimed


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (requesting_department) REFERENCES  public.departments (id),
  FOREIGN KEY (issuing_department) REFERENCES  public.departments (id),
  FOREIGN KEY (requested_by) REFERENCES  hrm.employees (id),
  FOREIGN KEY (prepared_by) REFERENCES  hrm.employees (id),
  FOREIGN KEY (dispensed_by) REFERENCES  hrm.employees (id),
  FOREIGN KEY (claimed_by) REFERENCES  hrm.employees (id)
);