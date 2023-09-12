alter table billing.price_tier_details
    drop column if exists percentage_value,
	add column medicine_percentage numeric,
	add column supplies_percentage numeric,
	add column service_percentage numeric;



