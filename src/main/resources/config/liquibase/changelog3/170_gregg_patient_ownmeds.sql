CREATE TABLE "pms"."patient_own_medicines" (
	"id" uuid NOT NULL,
	"medicine_name" varchar,
	"qty_onhand" numeric,
	"linked_medication" uuid,
	"case" uuid,
	"active" bool,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

ALTER TABLE "inventory"."stock_request_item"
	ADD COLUMN "medication" uuid;