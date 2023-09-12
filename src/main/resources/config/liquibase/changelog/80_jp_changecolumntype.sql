ALTER TABLE "inventory"."purchase_order"
  ALTER COLUMN "po_number" TYPE varchar USING "po_number"::varchar;