
CREATE VIEW "inventory"."inventory" AS select item,department, sum(quantity) from "inventory"."inventory_ledger" GROUP BY item,department;
