CREATE TABLE "inventory"."stock_request_item" (
	"id" uuid NOT NULL,
	"item_description" varchar,
	"expected_barcode" varchar,
	"item_reference_id" uuid,
	"requested_qty" numeric,
	"prepared_qty" numeric,
	"stock_request" uuid,
	"ordered_by" varchar,
	"ordered_by_reference_id" uuid,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

CREATE TABLE "inventory"."stock_request" (
	"id" uuid NOT NULL,
	"patient" varchar,
	"patient_reference_id" uuid,
	"stock_request_no" numeric,
	"status" varchar,
	"prepared_by" varchar,
	"prepared_datetime" timestamp NULL,
	"prepared_by_reference_id" uuid,
	"requested_by" varchar,
	"requested_by_reference_id" uuid,
	"requested_by_datetime" timestamp NULL,
	"dispensed_by" varchar,
	"dispensed_by_reference_id" uuid,
	"dispensed_by_datetime" timestamp NULL,
	"requesting_department" varchar,
	"request_type" varchar,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

drop table "public"."stock_request_item";
drop table "public"."stock_request";