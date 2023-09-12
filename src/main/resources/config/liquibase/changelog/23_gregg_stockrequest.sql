ALTER TABLE "inventory"."stock_request"
  ADD COLUMN "created_by" varchar(50) COLLATE "default",
  ADD COLUMN "created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "last_modified_by" varchar(50) COLLATE "default",
  ADD COLUMN "last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE "inventory"."stock_request_item"
  ADD COLUMN "created_by" varchar(50) COLLATE "default",
  ADD COLUMN "created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "last_modified_by" varchar(50) COLLATE "default",
  ADD COLUMN "last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP;