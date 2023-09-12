CREATE TABLE "inventory"."department_item" (
	"id" uuid NOT NULL,
	"item" uuid NOT NULL,
	"department" uuid NOT NULL,
	"reorder_quantity" numeric,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);