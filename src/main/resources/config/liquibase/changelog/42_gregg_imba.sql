ALTER TABLE "inventory"."item" DROP COLUMN "minimum_level";
ALTER TABLE "inventory"."item"
	ADD COLUMN "minimum_level" numeric;