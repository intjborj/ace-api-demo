drop table if exists "inventory"."stock_request_item";
drop table if exists "inventory"."stock_request";

CREATE TABLE "inventory"."stock_request" (
	"id" uuid NOT NULL,
	
	"stock_request_no" varchar,
	"status" varchar,
    "patient" uuid,

	"requesting_department" uuid,
	"requested_department" uuid,

	"requested_by" uuid,
	"requested_by_datetime" timestamp,

	"prepared_by" uuid,
	"prepared_by_datetime" timestamp,

	"dispensed_by" uuid,
	"dispensed_by_datetime" timestamp,

	"claimed_by" uuid,
	"claimed_by_datetime" timestamp,

	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);

ALTER TABLE "inventory"."stock_request"
  ADD COLUMN "created_by" varchar(50) COLLATE "default",
  ADD COLUMN "created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "last_modified_by" varchar(50) COLLATE "default",
  ADD COLUMN "last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE "inventory"."stock_request"
    ADD CONSTRAINT "fk_sr_patient"
    FOREIGN KEY ("patient")
    REFERENCES "pms"."patients" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE "inventory"."stock_request"
    ADD CONSTRAINT "fk_sr_requesting_dept"
    FOREIGN KEY ("requesting_department")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "inventory"."stock_request"
    ADD CONSTRAINT "fk_sr_requested_dept"
    FOREIGN KEY ("requested_department")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE "inventory"."stock_request"
    ADD CONSTRAINT "fk_sr_requested_by_emp"
    FOREIGN KEY ("requested_by")
    REFERENCES "hrm"."employees" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "inventory"."stock_request"
    ADD CONSTRAINT "fk_sr_prepared_by_emp"
    FOREIGN KEY ("prepared_by")
    REFERENCES "hrm"."employees" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "inventory"."stock_request"
    ADD CONSTRAINT "fk_sr_dispensed_by_emp"
    FOREIGN KEY ("dispensed_by")
    REFERENCES "hrm"."employees" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "inventory"."stock_request"
    ADD CONSTRAINT "fk_sr_claimed_by_emp"
    FOREIGN KEY ("claimed_by")
    REFERENCES "hrm"."employees" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;