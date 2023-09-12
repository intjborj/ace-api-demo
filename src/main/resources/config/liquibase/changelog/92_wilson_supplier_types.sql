
create table inventory.supplier_types
(
  id                             uuid not null primary key,
  supplier_type_code             varchar,
  sup_sub_account_code           varchar,
  supplier_type_description      varchar,
  sup_ewt_rate                   int4,
  is_active                      bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool
);