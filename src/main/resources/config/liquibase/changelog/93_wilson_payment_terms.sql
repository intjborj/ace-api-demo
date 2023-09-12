
create table inventory.payment_terms
(
  id                             uuid not null primary key,
  payment_term_code              varchar,
  payment_term_description       varchar,
  payment_term_days              int4,
  is_active                      bool,


  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool
);