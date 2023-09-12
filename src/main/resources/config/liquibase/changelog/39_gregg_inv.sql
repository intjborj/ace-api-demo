CREATE OR REPLACE VIEW "inventory"."inventory" AS  SELECT inventory_ledger.item,
    inventory_ledger.department,
    sum(inventory_ledger.quantity) AS quantity
   FROM inventory_ledger
  GROUP BY inventory_ledger.item, inventory_ledger.department;
COMMENT ON VIEW "inventory"."inventory" IS NULL;