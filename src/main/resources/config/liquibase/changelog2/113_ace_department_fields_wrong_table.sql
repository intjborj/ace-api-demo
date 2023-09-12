alter table departments
	drop is_vatable,
	drop vat_rate;

alter table billing.price_tier_details
	add is_vatable boolean,
	add vat_rate numeric;
