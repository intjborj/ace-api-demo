alter table pms.cases
	add column price_tier_detail uuid;

alter table pms.cases
  add constraint fk_cases_price_tier_details
    foreign key (price_tier_detail)
      references billing.price_tier_details (id)
      on update cascade on delete restrict;