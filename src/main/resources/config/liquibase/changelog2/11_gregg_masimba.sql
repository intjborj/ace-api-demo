DROP TABLE if exists "inventory"."stock_request";

CREATE TABLE "inventory"."stock_request" (
	"id" uuid NOT NULL,
	"stock_request_no" varchar COLLATE "default",
	"status" varchar COLLATE "default",
	"patient" uuid,
	"requesting_department" uuid,
	"requested_department" uuid,
	"requested_by" uuid,
	"requested_by_datetime" timestamp(6) NULL,
	"prepared_by" uuid,
	"prepared_by_datetime" timestamp(6) NULL,
	"dispensed_by" uuid,
	"dispensed_by_datetime" timestamp(6) NULL,
	"claimed_by" uuid,
	"claimed_by_datetime" timestamp(6) NULL,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);