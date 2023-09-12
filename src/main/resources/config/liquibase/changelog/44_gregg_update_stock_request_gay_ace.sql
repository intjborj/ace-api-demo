ALTER TABLE "inventory"."stock_request"
	ADD COLUMN "requested_department" varchar,
	ADD COLUMN "requesting_department_reference_id" uuid,
	ADD COLUMN "requested_department_reference_id" uuid;