-- not sure but I made it to wrong spelling "servce" to avoid possible conflict on reserved words
create table billing.service_price_controls
(
  id uuid not null primary key,
  amount_value     numeric,
  "price_tier_detail"             uuid
    constraint fk_items_price_control_price_tier_details
      references billing.price_tier_details(id)
      on update cascade on delete restrict,
  "servce"             uuid
    constraint fk_price_control_services
      references ancillary.services(id)
      on update cascade on delete restrict,
  created_by         varchar(50),
  created_date       timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by   varchar(50),
  last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);