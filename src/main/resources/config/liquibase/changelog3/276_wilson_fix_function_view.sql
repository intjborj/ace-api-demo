CREATE OR REPLACE FUNCTION inventory.last_wcost(itemid uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE last_wcost numeric(9, 2);
BEGIN
		last_wcost = (
            select
		    CASE
		        WHEN a.document_types = 'd12f0de2-cb65-42ab-bcdb-881ebce57045'
		            THEN a.ledger_unit_cost
		        WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
		            THEN abs(a.ledger_unit_cost)
		            ELSE  COALESCE(round((sum(a.ledger_unit_cost * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date)) / NULLIF((sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date)),0),2),0)
		    END as wcost

		    from inventory.inventory_ledger a

		    where
		    --a.source_dep = depid and
		    a.item = itemid
		    and a.is_include = true

		    group by
		    a.document_types,
		    a.ledger_date,
		    a.ledger_qty_in,
		    a.ledger_qty_out,
		    a.ledger_unit_cost

		    order by a.ledger_date desc limit 1
		);
		RETURN last_wcost;
END; $function$
;


CREATE OR REPLACE FUNCTION inventory.last_wcost_by_date(itemid uuid, filterdate date)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE last_wcost numeric(9, 2);
BEGIN
		last_wcost = (
			select
		    CASE
		        WHEN a.document_types = 'd12f0de2-cb65-42ab-bcdb-881ebce57045'
		            THEN a.ledger_unit_cost
		        WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
		            THEN abs(a.ledger_unit_cost)
		            ELSE  COALESCE(round((sum(a.ledger_unit_cost * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date)) / NULLIF((sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date)),0),2),0)
		    END as wcost

		    from inventory.inventory_ledger a

		    where
		    --a.source_dep = depid and
		    a.item = itemid
		    and date(a.ledger_date) <= filterdate
            and a.is_include = true

		    group by
		    a.document_types,
		    a.ledger_date,
		    a.ledger_qty_in,
		    a.ledger_qty_out,
		    a.ledger_unit_cost

		    order by a.ledger_date desc limit 1
		);
		RETURN last_wcost;
END; $function$
;


CREATE OR REPLACE VIEW inventory.inventory
AS SELECT department_item.id,
    department_item.item,
    department_item.department,
    department_item.reorder_quantity,
    department_item.allow_trade,
    COALESCE(( SELECT inventory.onhand(department_item.department, department_item.item) AS onhand), 0) AS onhand,
    COALESCE(( SELECT inventory.last_unit_price(department_item.item) AS last_unit_price), 0::numeric) AS last_unit_cost,
    COALESCE(( SELECT inventory.last_wcost(department_item.item) AS last_wcost), 0::numeric) AS last_wcost,
    COALESCE(( SELECT inventory.expiry_date(department_item.item) AS expiry_date), NULL::date) AS expiration_date,
    item.desc_long,
    item.sku,
    item.item_code,
    item.active,
    department_item.department AS dep_id,
    department_item.item AS item_id
   FROM inventory.department_item,
    inventory.item
  WHERE department_item.item = item.id;
