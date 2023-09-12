create table pms.case_insurances
(
  id                     uuid not null primary key,
  parent_case            uuid
  constraint fk_case_insurances_cases
  references pms.cases
  on update cascade on delete restrict,

  insurance_company            uuid
  constraint fk_case_insurance_insurance_companies
  references philhealth.insurance_companies
  on update cascade on delete restrict,

  reference_no            varchar,
  covered_amount          numeric,

  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool
);