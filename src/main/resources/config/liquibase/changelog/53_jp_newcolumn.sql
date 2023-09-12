ALTER TABLE "inventory"."purchase_request"
  ADD COLUMN "approved_by" uuid,
  ADD COLUMN "approved_name" varchar(255),
  ADD COLUMN "suggested_supplier" uuid,
  ADD COLUMN "suggested_supplier_name" varchar(255),
  ADD COLUMN "approved_date" timestamp(0),
  ADD COLUMN "request_type" varchar(255);

  ALTER TABLE "inventory"."purchase_request_items" RENAME COLUMN "item_ name" TO "item_name";