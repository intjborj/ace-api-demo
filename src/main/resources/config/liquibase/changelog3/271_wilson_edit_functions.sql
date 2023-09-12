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
		    and a.is_include = true

		    group BY
		    a.id,
		    a.ledger_date,
		    a.ledger_qty_in,
		    a.ledger_qty_out

		    order by a.ledger_date desc limit 1), 0) as onHand);
		RETURN qty;
END; $function$
;


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
            and a.is_include = true

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


CREATE OR REPLACE FUNCTION inventory.last_wcost_by_date(depid uuid, itemid uuid, filterdate date)
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

		    where a.source_dep = depid and a.item = itemid
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

CREATE OR REPLACE FUNCTION inventory.last_wcost(depid uuid, itemid uuid)
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

		    where a.source_dep = depid and a.item = itemid
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


CREATE OR REPLACE FUNCTION inventory.last_unit_price_by_date(itemid uuid, filterdate date)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE last_unit_cost numeric(9, 2);
BEGIN
		last_unit_cost = (
            select
		    a.ledger_unit_cost as unitcost

		    from inventory.inventory_ledger a

		    where a.item = itemid and a.document_types IN ('254a07d3-e33a-491c-943e-b3fe6792c5fc', '0caab388-e53b-4e94-b2ea-f8cc47df6431', 'af7dc429-8352-4f09-b58c-26a0a490881c', '27d236bb-c023-44dc-beac-18ddfe1daf79', '37683c86-3038-4207-baf0-b51456fd7037')
		    and date(a.ledger_date) <= filterdate
		    and a.is_include = true
			--document Types (SRR, BEG, EP, MP, PHY)
		    group by
		    a.ledger_date,
		    a.ledger_unit_cost

		    order by a.ledger_date desc limit 1
        );
		RETURN last_unit_cost;
END; $function$
;


CREATE OR REPLACE FUNCTION inventory.last_unit_price(itemid uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE last_unit_cost numeric;
BEGIN
		last_unit_cost = (
            select
		    a.ledger_unit_cost as unitcost

		    from inventory.inventory_ledger a

		    where a.item = itemid and a.document_types IN ('254a07d3-e33a-491c-943e-b3fe6792c5fc', '0caab388-e53b-4e94-b2ea-f8cc47df6431', 'af7dc429-8352-4f09-b58c-26a0a490881c', '27d236bb-c023-44dc-beac-18ddfe1daf79', '37683c86-3038-4207-baf0-b51456fd7037')
			and a.is_include = true
			--document Types (SRR, BEG, EP, MP, PHY)

		    group by
			a.ledger_date,
		    a.ledger_unit_cost

		    order by a.ledger_date desc limit 1
        );
		RETURN last_unit_cost;
END; $function$
;



