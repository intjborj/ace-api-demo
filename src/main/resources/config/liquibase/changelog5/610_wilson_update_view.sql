 CREATE OR REPLACE VIEW inventory.inventory_supplier
AS SELECT a.id,
    a.item_id,
    b.desc_long,
    b.sku,
    b.item_code,
    a.supplier,
    di.department as source_dep,
    a.cost AS unit_cost,
    COALESCE(c.onhand, 0::bigint) AS onhand
   FROM inventory.supplier_item a
   	 LEFT JOIN inventory.department_item di ON di.item = a.item_id
     LEFT JOIN inventory.onhandref c ON c.item = a.item_id and c.source_dep = di.department
     LEFT JOIN inventory.item b ON b.id = a.item_id
  WHERE (a.deleted = false OR a.deleted IS NULL) AND b.active = true;