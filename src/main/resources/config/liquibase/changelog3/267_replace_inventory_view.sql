CREATE OR REPLACE VIEW inventory.inventory
AS SELECT department_item.id,
    department_item.item,
    department_item.department,
    department_item.reorder_quantity,
    department_item.allow_trade,
    COALESCE(( SELECT inventory.onhand(department_item.department, department_item.item) AS onhand), 0) AS onhand,
    COALESCE(( SELECT inventory.last_unit_price(department_item.item) AS last_unit_price), 0::numeric) AS last_unit_cost,
    COALESCE(( SELECT inventory.last_wcost(department_item.department, department_item.item) AS last_wcost), 0::numeric) AS last_wcost,
    COALESCE(( SELECT inventory.expiry_date(department_item.item) AS expiry_date), NULL::date) AS expiration_date,
    item.desc_long,
    item.sku,
    item.item_code,
    item.active,
    department_item.department as dep_id,
    department_item.item as item_id
   FROM inventory.department_item, inventory.item where department_item.item = item.id;