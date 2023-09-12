CREATE TABLE "inventory"."inventory_ledger" (
	"id" uuid NOT NULL,
	"item" uuid,
	"department" uuid,
	"quantity" numeric,
	"type" varchar,
	"source" varchar,
	"receiving_reference" varchar,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NOT NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("id", "last_modified_date") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);