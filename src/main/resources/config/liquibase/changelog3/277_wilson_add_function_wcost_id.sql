CREATE OR REPLACE FUNCTION inventory.last_wcost_by_id(itemid uuid, invid uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE last_wcost numeric;
BEGIN
		last_wcost = (
            with a as (select
			a.id,
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
		    a.id,
		    a.document_types,
		    a.ledger_date,
		    a.ledger_qty_in,
		    a.ledger_qty_out,
		    a.ledger_unit_cost
		    
		    order by a.ledger_date desc)
		    select wcost from a where id = invid
        );
		RETURN last_wcost;
END; $function$
;