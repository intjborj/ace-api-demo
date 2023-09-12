ALTER TABLE "inventory"."stock_request_item"
	ADD COLUMN "deducted_to_inventory" numeric,
	ADD COLUMN "billed_to_patient" bool;