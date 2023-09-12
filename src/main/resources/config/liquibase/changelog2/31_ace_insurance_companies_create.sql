create table philhealth.insurance_companies
(
  id                                uuid not null primary key,
  company_name                      varchar,
  company_address                   varchar,
  company_contact_name              varchar,
  company_contact_no                varchar,
  company_email                     varchar,
  company_type                      varchar,
  is_active                         bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool
);