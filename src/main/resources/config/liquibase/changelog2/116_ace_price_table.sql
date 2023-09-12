create table billing.item_price_controls
(
  id uuid not null primary key,
  amount_value     numeric,
  "price_tier_detail"             uuid
    constraint fk_items_price_control_price_tier_details
      references billing.price_tier_details(id)
      on update cascade on delete restrict,
  "item"             uuid
    constraint fk_price_control_items
      references inventory.item(id)
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);