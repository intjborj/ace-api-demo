drop table if exists "inventory"."stock_issue" CASCADE;
create table inventory.stock_issue
(
  id                            uuid not null primary key,
  issue_no                      varchar,
  issue_date                    timestamp without time zone,
  issue_from                    uuid,
  issue_to                      uuid,
  issued_by                     uuid,
  is_posted                     bool,
  is_cancel                     bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (issue_from) REFERENCES  public.departments (id),
  FOREIGN KEY (issue_to) REFERENCES  public.departments (id),
  FOREIGN KEY (issued_by) REFERENCES  hrm.employees (id)
);