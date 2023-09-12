CREATE OR REPLACE FUNCTION inventory.onhand_by_date(depid uuid, itemid uuid, filterdate date)
 RETURNS integer
 LANGUAGE plpgsql
AS $function$
DECLARE qty integer;
BEGIN
		qty = (
		    with stockcard as(
select
	a.id,
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
	case
		when a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
		or a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' then 0
		else a.ledger_qty_in end as ledger_qtyin,
		a.ledger_qty_out,
		case
			when a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e'
			or a.document_types = '37683c86-3038-4207-baf0-b51456fd7037' then a.ledger_qty_in
			else 0 end as adjustment,
			a.ledger_unit_cost as Unitcost,
			sum(a.ledger_qty_in - a.ledger_qty_out) over (
			order by a.ledger_date) as RunningQty,
			case
				when a.document_types = 'd12f0de2-cb65-42ab-bcdb-881ebce57045' then a.ledger_unit_cost
				when a.document_types = '4f88d8d7-ecce-4538-a97b-88884b1e106e' then abs(a.ledger_unit_cost)
				else coalesce(round((sum(a.ledger_unit_cost * (sum(a.ledger_qty_in - a.ledger_qty_out))) over (order by a.ledger_date)) / nullif((sum(a.ledger_qty_in - a.ledger_qty_out) over (order by a.ledger_date)), 0), 2), 0) end as wcost,
				sum(abs(a.ledger_unit_cost) * (sum(a.ledger_qty_in - a.ledger_qty_out))) over (
				order by a.ledger_date) as RunningBalance
			from
				inventory.inventory_ledger a
			inner join public.departments b on
				a.source_dep = b.id
			inner join public.departments c on
				a.destination_dep = c.id
			inner join inventory.document_types d on
				a.document_types = d.id
			inner join inventory.item e on
				a.item = e.id
			where
				a.source_dep = depId
				and a.item = itemId
			group by
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
)select
	coalesce ((
	select
		RunningQty
	from
		stockcard
	where
		date(ledger_date) <= filterDate
	order by
		ledger_date desc
	limit 1),
	0)
		);
		RETURN qty;
END; $function$
;
