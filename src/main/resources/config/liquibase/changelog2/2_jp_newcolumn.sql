ALTER TABLE "inventory"."purchase_order"
  ADD COLUMN "department_from" uuid,
  ADD COLUMN "department_to" uuid;

  ALTER TABLE "inventory"."purchase_order"
  ALTER COLUMN "payment_terms" TYPE uuid USING "payment_terms"::uuid;