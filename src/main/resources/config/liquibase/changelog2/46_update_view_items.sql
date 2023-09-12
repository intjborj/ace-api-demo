drop view if exists "inventory"."view_items";
CREATE VIEW "inventory"."view_items" AS SELECT *,  COALESCE((SELECT inventory.last_unit_price(id)),0) as last_unit_cost FROM inventory.item;