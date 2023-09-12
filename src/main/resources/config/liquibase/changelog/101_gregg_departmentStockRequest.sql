CREATE TABLE "inventory"."department_stock_request" (
	"id" uuid NOT NULL,
	"requesting_department" uuid,
	"issuing_department" uuid,
	"status" varchar,
	"type" varchar,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted" bool,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

CREATE TABLE "inventory"."department_stock_request_items" (
	"id" uuid NOT NULL,
	"item" uuid,
	"department_stock_request" uuid,
	"quantity_requested" numeric,
	"unit_cost" numeric,
	"status" varchar,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted" bool,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);