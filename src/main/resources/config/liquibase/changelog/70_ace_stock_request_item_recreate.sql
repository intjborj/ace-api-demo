CREATE TABLE "inventory"."stock_request_item" (
	"id" uuid NOT NULL,
	"item_description" varchar,
	"expected_barcode" varchar,
	"item" uuid,
	"requested_qty" numeric,
	"prepared_qty" numeric,
	"stock_request" uuid,
	"ordered_by" uuid,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

ALTER TABLE "inventory"."stock_request_item"
  ADD COLUMN "created_by" varchar(50) COLLATE "default",
  ADD COLUMN "created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "last_modified_by" varchar(50) COLLATE "default",
  ADD COLUMN "last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE "inventory"."stock_request_item"
    ADD CONSTRAINT "fk_sri_item"
    FOREIGN KEY ("item")
    REFERENCES "inventory"."item" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "inventory"."stock_request_item"
    ADD CONSTRAINT "fk_sri_stock_request"
    FOREIGN KEY ("stock_request")
    REFERENCES "inventory"."stock_request" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "inventory"."stock_request_item"
    ADD CONSTRAINT "fk_sri_employee"
    FOREIGN KEY ("ordered_by")
    REFERENCES "hrm"."employees" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;