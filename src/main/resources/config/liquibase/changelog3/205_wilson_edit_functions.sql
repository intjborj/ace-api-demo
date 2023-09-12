    CREATE OR REPLACE FUNCTION inventory.onhand(depid uuid, itemid uuid)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE qty integer;
BEGIN
		qty = (
			select
		    sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date) as onHand

		    from inventory.inventory_ledger a

		    where a.source_dep = depid and a.item = itemid

		    group BY
		    a.id,
		    a.ledger_date,
		    a.ledger_qty_in,
		    a.ledger_qty_out

		    order by a.ledger_date desc limit 1
		);
		RETURN qty;
END; $function$
;

CREATE OR REPLACE FUNCTION inventory.onhand_by_date(depid uuid, itemid uuid, filterdate date)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE qty integer;
BEGIN
		qty = (select
			coalesce ((
			select
		    sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date) as onHand

		    from inventory.inventory_ledger a

		    where a.source_dep = depid and a.item = itemid
		    and date(a.ledger_date) <= filterdate

		    group BY
		    a.id,
		    a.ledger_date,
		    a.ledger_qty_in,
		    a.ledger_qty_out

		    order by a.ledger_date desc limit 1), 0) as onHand);
		RETURN qty;
END; $function$
;
