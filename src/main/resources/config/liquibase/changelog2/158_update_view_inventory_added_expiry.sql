drop view if exists "inventory"."inventory";
CREATE VIEW "inventory"."inventory" AS
SELECT *,
COALESCE((SELECT inventory.onhand(department, item)),0) as onhand,
COALESCE((SELECT inventory.last_unit_price(item)),0) as last_unit_cost,
COALESCE((SELECT inventory.last_wcost(department, item)),0) as last_wcost,
COALESCE((SELECT inventory.expiry_date(item)),NULL) as expiration_date
 FROM inventory.department_item