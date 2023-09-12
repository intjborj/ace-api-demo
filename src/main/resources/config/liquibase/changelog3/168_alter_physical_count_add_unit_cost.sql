ALTER TABLE "inventory"."physical_count"
	ADD COLUMN "unit_cost" numeric default 0,
	ADD COLUMN "wcost" numeric default 0;
