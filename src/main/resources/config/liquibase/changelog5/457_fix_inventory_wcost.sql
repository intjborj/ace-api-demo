DROP VIEW IF EXISTS inventory.inventory;
DROP VIEW IF EXISTS inventory.wcostref;
DROP VIEW IF EXISTS inventory.inventory_supplier;
DROP VIEW IF EXISTS inventory.onhandref;

CREATE OR REPLACE VIEW inventory.onhandref
AS SELECT a.source_dep,
    a.item,
    sum(a.ledger_qty_in - a.ledger_qty_out) AS onhand
   FROM inventory.inventory_ledger a
  WHERE a.is_include = true
  GROUP BY a.source_dep, a.item;

-- inventory.inventory_supplier source

CREATE OR REPLACE VIEW inventory.inventory_supplier
AS SELECT a.id,
    a.item_id,
    b.desc_long,
    b.sku,
    b.item_code,
    a.supplier,
    c.source_dep,
    a.cost AS unit_cost,
    COALESCE(c.onhand, 0::bigint) AS onhand
   FROM inventory.supplier_item a
     LEFT JOIN inventory.onhandref c ON c.item = a.item_id
     LEFT JOIN inventory.item b ON b.id = a.item_id
  WHERE (a.deleted = false OR a.deleted IS NULL) AND b.active = true;

CREATE OR REPLACE VIEW inventory.inventory
AS SELECT a.id,
    a.item,
    a.department,
    a.reorder_quantity,
    a.allow_trade,
    COALESCE(c.onhand, 0::bigint) AS onhand,
    COALESCE(round(d.unitcost, 4), 0::numeric) AS last_unit_cost,
    COALESCE(f.expiration_date, NULL::date) AS expiration_date,
    b.desc_long,
    b.sku,
    b.item_code,
    b.active,
    a.department AS dep_id,
    a.item AS item_id
   FROM inventory.department_item a
     LEFT JOIN inventory.onhandref c ON c.item = a.item AND c.source_dep = a.department
     LEFT JOIN inventory.unitcostref d ON d.item = a.item
     LEFT JOIN inventory.expiryref f ON f.item = a.item
     LEFT JOIN inventory.item b ON b.id = a.item
  WHERE a.is_assign = true;