drop view if exists "inventory"."view_items";
CREATE VIEW "inventory"."view_items" AS SELECT *,  COALESCE((SELECT inventory.last_unit_price(id)),0) as last_unit_cost FROM inventory.item;

drop view if exists "inventory"."inventory";
CREATE VIEW "inventory"."inventory" AS SELECT *, COALESCE((SELECT inventory.onhand(department, item)),0) as onhand, COALESCE((SELECT inventory.last_unit_price(item)),0) as last_unit_cost, COALESCE((SELECT inventory.last_wcost(department, item)),0) as last_wcost  FROM inventory.department_item;

