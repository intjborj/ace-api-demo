create table billing.price_tier_modifiers
(
  id                             uuid not null primary key,
  price_tier_detail              uuid not null,
  employee                       uuid not null,
  category_type                  varchar,
  from_cost                      numeric(15,2),
  to_cost                        numeric(15,2),
  test_amount                    numeric(15,2),
  percentage_value               numeric(15,2),

  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,

  FOREIGN KEY (price_tier_detail) REFERENCES billing.price_tier_details (id),
  FOREIGN KEY (employee) REFERENCES hrm.employees (id)
);