ALTER TABLE billing.companyaccounts
ADD COLUMN company_type UUID;

create table billing.company_type
(
  id                                uuid not null primary key,
  code                              varchar,
  description                       varchar,
  active                            BOOL,

  deleted                           BOOL,
  deleted_date                      timestamp(6) default CURRENT_TIMESTAMP,
  created_by                        varchar(50),
  created_date                      timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by                  varchar(50),
  last_modified_date                timestamp(6) default CURRENT_TIMESTAMP
);