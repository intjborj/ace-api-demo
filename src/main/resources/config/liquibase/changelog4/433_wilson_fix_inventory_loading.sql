-- inventory.wcostref source
drop view if exists inventory.inventory;
DROP VIEW IF EXISTS inventory.wcostref;
CREATE OR REPLACE VIEW inventory.wcostref
AS SELECT i.id,
    b.source_dep,
    coalesce(a.unitcost *  b.onhand / nullif(b.onhand,0), coalesce(a.unitcost,0)) as wcost
   FROM inventory.item i
   LEFT JOIN inventory.unitcostref a ON a.item = i.id
    LEFT JOIN inventory.onhandref b ON b.item = i.id
  WHERE i.active = true;

CREATE OR REPLACE VIEW inventory.inventory
AS SELECT a.id,
    a.item,
    a.department,
    a.reorder_quantity,
    a.allow_trade,
    COALESCE(c.onhand, 0) AS onhand,
    COALESCE(d.unitcost, 0::numeric) AS last_unit_cost,
	COALESCE(d.unitcost * c.onhand / NULLIF(c.onhand, 0), COALESCE(d.unitcost, 0)) AS last_wcost,
    COALESCE(f.expiration_date, NULL::date) AS expiration_date,
    b.desc_long,
    b.sku,
    b.item_code,
    b.active,
    a.department AS dep_id,
    a.item AS item_id
   FROM inventory.department_item a
    LEFT JOIN inventory.onhandref c ON c.item = a.item and c.source_dep = a.department
    LEFT JOIN inventory.unitcostref d ON d.item = a.item
    LEFT JOIN inventory.expiryref f ON f.item = a.item
    LEFT JOIN inventory.item b ON b.id = a.item
    where a.is_assign = true;