ALTER TABLE "inventory"."item" DROP COLUMN "prepared_by",
	DROP COLUMN "prepared_date";
ALTER TABLE "inventory"."item"
	ADD COLUMN "deleted" bool;