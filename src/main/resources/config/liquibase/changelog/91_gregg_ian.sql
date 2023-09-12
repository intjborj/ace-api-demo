ALTER TABLE "inventory"."supplier"
	ADD COLUMN "tin" varchar,
	ADD COLUMN "status" varchar,
	ADD COLUMN "supplier_category" varchar,
	ADD COLUMN "credit_limit" numeric,
	ADD COLUMN "vat_inclusive" bool,
	ADD COLUMN "telephone_no" varchar;