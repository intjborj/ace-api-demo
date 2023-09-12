drop view if exists "inventory"."view_physical_count";
CREATE VIEW "inventory"."view_physical_count" AS select pc.id,
pc.date_trans,
pc.department ,
i.desc_long ,
big.unit_description as unit_of_purchase,
small.unit_description as unit_of_usage,
ic.category_description,
pc.expiration_date,
pc.on_hand ,
(select COALESCE(sum(p.log_count), 0) from inventory.physical_logs_count p where p.physical_count = pc.id) as monthly_count,
pc.variance,
pc.unit_cost
from inventory.physical_count pc, inventory.item i, inventory.unit_measurements big,  inventory.unit_measurements small, inventory.item_categories ic
where
pc.item = i.id
and i.unit_of_usage = small.id
and i.unit_of_purchase  = big.id
and i.item_category = ic.id ;
