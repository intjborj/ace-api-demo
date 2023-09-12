CREATE TABLE "inventory"."return_medication" (
	"id" uuid NOT NULL,
	"patient" uuid,
	"case" uuid,
	"returned_by" uuid,
	"received_by" uuid,
	"remarks" varchar,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

CREATE TABLE "inventory"."return_medication_items" (
	"id" uuid NOT NULL,
	"return_medication" uuid,
	"medicine" uuid,
	"quantity_returned" numeric,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);