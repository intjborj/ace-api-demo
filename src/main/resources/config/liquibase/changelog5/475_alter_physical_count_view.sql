CREATE OR REPLACE VIEW inventory.plogcountref
AS select
    plc.physical_count,
    sum(plc.log_count) as monthly_count
 from inventory.physical_logs_count plc
 where plc.deleted is null or plc.deleted = false
 GROUP BY plc.physical_count;


 -- inventory.view_physical_count source
DROP VIEW IF EXISTS  inventory.view_physical_count;
CREATE OR REPLACE VIEW inventory.view_physical_count
AS SELECT pc.id,
	pc.physical_count_transaction,
    pc.date_trans,
    pc.department,
    i.sku,
    i.desc_long,
    big.unit_description AS unit_of_purchase,
    small.unit_description AS unit_of_usage,
    ic.category_description,
    pc.expiration_date,
    pc.on_hand,
   	coalesce(pl.monthly_count, 0) as monthly_count,
    pc.variance,
    pc.unit_cost,
    pc.is_posted,
    pc.is_cancel
   FROM inventory.physical_count pc
   	left join inventory.plogcountref pl on pl.physical_count = pc.id
    left join inventory.item i on pc.item = i.id
    left join inventory.unit_measurements big on i.unit_of_purchase = big.id
    left join inventory.unit_measurements small on i.unit_of_usage = small.id
    left join inventory.item_categories ic on i.item_category = ic.id;