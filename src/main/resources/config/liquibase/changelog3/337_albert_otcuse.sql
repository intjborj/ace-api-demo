ALTER TABLE billing.price_tier_details ADD oct_use boolean NULL;
update billing.price_tier_details set oct_use = true where tier_code in ('STD-1','STD_2')