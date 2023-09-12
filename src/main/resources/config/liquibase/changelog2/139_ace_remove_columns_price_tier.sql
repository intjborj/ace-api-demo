ALTER TABLE "billing"."price_tier_details"
	DROP COLUMN "medicine_percentage",
	DROP COLUMN "supplies_percentage",
	DROP COLUMN "service_percentage";



ALTER TABLE "pms"."cases"
	ADD COLUMN "price_accommodation_type" varchar;