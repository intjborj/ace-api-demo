create table accounting.billing_schedule
(
  id                             uuid not null primary key,
  billing_schedule_no            varchar,
  account_id                     uuid,
  total_receivable_amount        numeric,
  status                         varchar,
  remarks                        varchar,
  transaction_date               timestamp(6) default CURRENT_TIMESTAMP,
  posted_date                    timestamp(6) default CURRENT_TIMESTAMP,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);

create table accounting.billing_schedule_items
(
  id                             uuid not null primary key,
  billing_schedule_id            uuid,
  billing_id                     uuid,
  billing_item_id                uuid,
  type                           varchar,
  amount                         numeric,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);