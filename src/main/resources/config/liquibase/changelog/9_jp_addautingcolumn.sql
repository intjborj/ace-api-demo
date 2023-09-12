ALTER TABLE "inventory"."receiving_report"
  ADD COLUMN "created_by" varchar(50) COLLATE "pg_catalog"."default",
  ADD COLUMN "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  ADD COLUMN "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE "inventory"."receiving_report_items"
  ADD COLUMN "created_by" varchar(50) COLLATE "pg_catalog"."default",
  ADD COLUMN "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  ADD COLUMN "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP;