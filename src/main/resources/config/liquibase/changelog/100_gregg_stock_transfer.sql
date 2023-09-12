CREATE TABLE "inventory"."stock_transfer" (
	"id" uuid NOT NULL,
	"issuing_department" uuid,
	"receiving_department" uuid,
	"issuing_person" uuid,
	"receiving_person" uuid,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted" bool,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

CREATE TABLE "inventory"."stock_transfer_item" (
	"id" uuid NOT NULL,
	"stock_transfer" uuid,
	"item" uuid,
	"quantity" numeric,
	"unit_cost" numeric,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);