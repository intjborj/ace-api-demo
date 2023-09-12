CREATE OR REPLACE FUNCTION inventory.inventory_by_date(trans_date date, depid uuid)
 RETURNS TABLE(id uuid,item uuid,department uuid,reorder_quantity numeric,allow_trade bool,onhand integer,last_unit_cost numeric,last_wcost numeric,expiration_date date,desc_long varchar,sku varchar,item_code varchar,active bool,dep_id uuid,item_id uuid)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  SELECT department_item.id,
    department_item.item,
    department_item.department,
    department_item.reorder_quantity,
    department_item.allow_trade,
    COALESCE(( SELECT inventory.onhand_by_date(department_item.department, department_item.item,trans_date) AS onhand), 0) AS onhand,
    COALESCE(( SELECT inventory.last_unit_price_by_date(department_item.item,trans_date) AS last_unit_price), 0::numeric) AS last_unit_cost,
    COALESCE(( SELECT inventory.last_wcost_by_date(department_item.item,trans_date) AS last_wcost), 0::numeric) AS last_wcost,
    COALESCE(( SELECT inventory.expiry_date(department_item.item) AS expiry_date), NULL::date) AS expiration_date,
    item.desc_long,
    item.sku,
    item.item_code,
    item.active,
    department_item.department AS dep_id,
    department_item.item AS item_id
   FROM inventory.department_item,
    inventory.item
  WHERE department_item.item = item.id and department_item.department = depid;
END;
$function$
;