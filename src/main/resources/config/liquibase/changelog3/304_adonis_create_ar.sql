create table accounting.account_receivable
(
  id                             uuid not null primary key,
  ar_no                          varchar,
  billing_schedule_id            uuid,
  account_id                     uuid,
  total_receivable_amount        numeric,
  balance                        numeric,
  status                         varchar,
  remarks                        varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);

create table accounting.account_receivable_items
(
  id                             uuid not null primary key,
  billing_schedule_item_id       uuid,
  account_receivable_id          uuid,
  billing_id                     uuid,
  billing_item_id                uuid,
  type                           varchar,
  amount                         numeric,
  balance                        numeric,
  status                         varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);
