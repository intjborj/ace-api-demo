DROP VIEW "inventory"."inventory";

CREATE OR REPLACE VIEW "inventory"."inventory" AS  SELECT "inventory".inventory_ledger.item as id, "inventory".inventory_ledger.item,
    "inventory".inventory_ledger.department,
    sum("inventory".inventory_ledger.quantity) AS quantity
   FROM "inventory".inventory_ledger
  GROUP BY "inventory".inventory_ledger.item, "inventory".inventory_ledger.department;
COMMENT ON VIEW "inventory"."inventory" IS NULL;