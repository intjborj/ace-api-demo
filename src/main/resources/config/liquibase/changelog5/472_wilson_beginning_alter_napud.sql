CREATE OR REPLACE VIEW inventory.item_without_beg
AS WITH beg AS (
         SELECT di.id,
            di.item,
            di.department,
            di.reorder_quantity,
            di.allow_trade,
            di.is_assign,
            il.reference_no,
            il.document_types,
    		COALESCE(round(d.unitcost, 4), 0::numeric) AS last_unit_cost
           FROM inventory.department_item di
             LEFT JOIN inventory.inventory_ledger il ON il.item = di.item AND il.source_dep = di.department AND il.document_types = '0caab388-e53b-4e94-b2ea-f8cc47df6431'::uuid and il.is_include = true
     		 LEFT JOIN inventory.unitcostref d ON d.item = di.item
          WHERE di.is_assign = true
        )
 SELECT b.id,
    b.item,
    b.department,
    b.reorder_quantity,
    b.allow_trade,
    b.is_assign,
    b.reference_no,
    b.document_types,
    b.last_unit_cost
   FROM beg b
  WHERE b.document_types IS NULL AND b.reference_no IS NULL;