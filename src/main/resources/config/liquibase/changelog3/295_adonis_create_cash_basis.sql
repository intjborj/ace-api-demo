create table inventory.cash_basis
(
  id                             uuid not null primary key,
  patient_id                     uuid,
  case_id                        uuid,
  department_id                  uuid,
  cash_basis_no                  varchar,
  status                         varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);

create table inventory.cash_basis_item
(
  id                             uuid not null primary key,
  or_number                      varchar,
  sri_number                     varchar,
  cash_basis_id                  uuid,
  item_id                        uuid,
  quantity                       int,
  price                          numeric,
  type                           varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);