CREATE TABLE "inventory"."purchase_request" (
  "id" uuid NOT NULL,
  "department" uuid,
  "department_name" varchar(255),
  "pr_no" varchar(255),
  "requested_by" uuid,
  "requested_by_name" varchar ,
  "requested_date" timestamp ,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY ("id")
)
;

CREATE TABLE "inventory"."purchase_request_items" (
  "id" uuid NOT NULL,
  "ref_item" uuid,
  "ref_pr" uuid,
  "item_ name" varchar(255),
  "qty" numeric,
  "price" decimal(10,2),
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;