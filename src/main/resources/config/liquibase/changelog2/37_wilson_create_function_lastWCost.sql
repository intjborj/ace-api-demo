drop FUNCTION if exists "inventory"."last_wcost";
CREATE FUNCTION	inventory.last_wcost(IN depId uuid, IN itemId uuid)
RETURNS integer
LANGUAGE 'plpgsql'
AS $$
DECLARE qty integer;
BEGIN
		qty = (
		WITH stockcard as(
            select a.id,
            a.source_dep,
            b.department_name as source_department,
            a.destination_dep,
            c.department_name as dest_department,
            a.document_types,
            d.document_code,
            d.document_desc,
            a.item,
            e.sku,
            e.item_code,
            e.desc_long,
            a.reference_no,
            a.ledger_date,
            CASE
                WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' OR a.document_types = '37683c86-3038-4207-baf0-b51456fd7037'
                THEN 0
                ELSE a.ledger_qty_in
            END as ledger_qtyin,
		    a.ledger_qty_out,
		    CASE
		        WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' OR a.document_types = '37683c86-3038-4207-baf0-b51456fd7037'
		        THEN a.ledger_qty_in
		        ELSE 0
		    END as adjustment,
		    a.ledger_unit_cost as Unitcost,
		    sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date) as RunningQty,
		    CASE
		        WHEN a.document_types = 'd12f0de2-cb65-42ab-bcdb-881ebce57045'
		            THEN a.ledger_unit_cost
		        WHEN a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
		            THEN abs(a.ledger_unit_cost)
		            ELSE  COALESCE(round((sum(a.ledger_unit_cost * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date)) / NULLIF((sum(a.ledger_qty_in - a.ledger_qty_out) OVER (ORDER BY a.ledger_date)),0),2),0)
		    END as wcost,
		    sum(abs(a.ledger_unit_cost) * (sum(a.ledger_qty_in - a.ledger_qty_out))) OVER (ORDER BY a.ledger_date) as RunningBalance

		    from inventory.inventory_ledger a
		    inner join public.departments b on a.source_dep = b.id
		    inner join public.departments c on a.destination_dep = c.id
		    inner join inventory.document_types d on a.document_types = d.id
		    inner join inventory.item e on a.item = e.id

		    where a.source_dep = depId and a.item = itemId

		    group BY
		    a.id,
		    a.source_dep,
		    b.department_name,
		    a.destination_dep,
		    c.department_name,
		    a.document_types,
		    d.document_code,
		    d.document_desc,
		    a.item,
		    e.sku,
		    e.item_code,
		    e.desc_long,
		    a.reference_no,
		    a.ledger_date,
		    a.ledger_qty_in,
		    a.ledger_qty_out,
		    a.ledger_unit_cost
		)SELECT wcost as last_wcost FROM stockcard ORDER BY ledger_date DESC LIMIT 1);
		RETURN qty;
END; $$