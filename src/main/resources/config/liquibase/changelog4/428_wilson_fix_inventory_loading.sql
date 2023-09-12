---- on hand view ---
create or replace view inventory.onhandref as select
a.source_dep,a.item,
sum(a.ledger_qty_in - a.ledger_qty_out) as onHand
from inventory.inventory_ledger a
where a.is_include = true
group BY
a.source_dep,a.item;
---- 

---- last unit cost view --
create or replace view inventory.unitcostref as with samp as (select
a.item,
LAST_VALUE(a.ledger_unit_cost) over (PARTITION BY a.item ORDER BY a.ledger_date RANGE BETWEEN 
            UNBOUNDED PRECEDING AND 
            UNBOUNDED following)  as unitcost
from inventory.inventory_ledger a
where 
a.document_types IN ('254a07d3-e33a-491c-943e-b3fe6792c5fc', 
'0caab388-e53b-4e94-b2ea-f8cc47df6431', 'af7dc429-8352-4f09-b58c-26a0a490881c', 
'27d236bb-c023-44dc-beac-18ddfe1daf79', '37683c86-3038-4207-baf0-b51456fd7037')
and a.is_include = true
--document Types (SRR, BEG, EP, MP, PHY)
group by
a.item,
a.ledger_unit_cost,
a.ledger_date)
select * from samp group by item, unitcost;
------

----last wcost ----
create or replace view inventory.wcostref as select i.id,
COALESCE(( SELECT inventory.last_wcost(i.id) AS last_wcost), 0::numeric) AS last_wcost
 from inventory.item i;
-----

----- expiry date ---
create or replace view inventory.expiryref as with expiry as (select
a.item,
LAST_VALUE(a.expiration_date) over (PARTITION BY a.item ORDER BY a.created_date RANGE BETWEEN 
            UNBOUNDED PRECEDING AND 
            UNBOUNDED following)  as expiration_date
from inventory.receiving_report_items a
group by a.item, a.expiration_date, a.created_date)
select * from expiry group by item, expiration_date;
-----
drop view if exists inventory.inventory;
CREATE OR REPLACE VIEW inventory.inventory
AS SELECT a.id,
    a.item,
    a.department,
    a.reorder_quantity,
    a.allow_trade,
    COALESCE(c.onhand, 0) AS onhand,
    COALESCE(d.unitcost, 0::numeric) AS last_unit_cost,
    COALESCE(e.last_wcost, 0::numeric) AS last_wcost,
    COALESCE(f.expiration_date, NULL::date) AS expiration_date,
    b.desc_long,
    b.sku,
    b.item_code,
    b.active,
    a.department AS dep_id,
    a.item AS item_id
   FROM inventory.department_item a
    LEFT JOIN inventory.item b ON b.id = a.item
    LEFT JOIN inventory.onhandref c ON c.item = a.item and c.source_dep = a.department
    LEFT JOIN inventory.unitcostref d ON d.item = a.item
    LEFT JOIN inventory.wcostref e ON e.id = a.item
    LEFT JOIN inventory.expiryref f ON f.item = a.item;
