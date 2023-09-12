ALTER TABLE "inventory"."receiving_report"
	ADD COLUMN "user_id" uuid,
	ADD COLUMN "user_fullname" varchar;