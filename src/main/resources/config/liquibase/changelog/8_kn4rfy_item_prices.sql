create table inventory.item_prices
(
  id                 uuid not null
    constraint item_prices_pkey
      primary key,
  p1                 numeric,
  p2                 numeric,
  p3                 numeric,
  p4                 numeric,
  p5                 numeric,
  p6                 numeric,
  p7                 numeric,
  selected           boolean      default false,
  item               uuid
    constraint fk_item_prices_items
      references inventory.items
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);