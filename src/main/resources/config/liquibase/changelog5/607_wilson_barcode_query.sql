drop function if exists inventory.barcode_supplier_item;
CREATE OR REPLACE FUNCTION inventory.barcode_supplier_item(sup uuid, _sku varchar)
RETURNS TABLE(id uuid, item uuid, unitCost numeric)
LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  select
  	a.id,
  	a.item_id as item,
  	a.cost as unitCost
  from inventory.supplier_item a
	LEFT JOIN inventory.item b ON b.id = a.item_id
  where a.supplier = sup
  and b.sku = _sku;
END;
$function$
;

drop function if exists inventory.barcode_issuance_item;
CREATE OR REPLACE FUNCTION inventory.barcode_issuance_item(dep uuid, _sku varchar)
RETURNS TABLE(id uuid, item uuid, wcost numeric)
LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  select
	a.id,
	a.item,
	(SELECT coalesce(inventory.last_wcost(a.item), 0)) AS wcost
  from inventory.department_item a
	LEFT JOIN inventory.item b ON b.id = a.item
  where b.sku = _sku
	and a.department = dep
	and is_assign = true;
END;
$function$
;