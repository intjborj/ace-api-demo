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
			--document Types (SRR, BEG, EP, MP, PHY)

		    group by
			a.ledger_date,
		    a.ledger_unit_cost

		    order by a.ledger_date desc limit 1
        );
		RETURN last_unit_cost;
END; $function$
;