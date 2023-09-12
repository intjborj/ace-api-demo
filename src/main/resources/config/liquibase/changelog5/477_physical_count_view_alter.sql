-- inventory.view_physical_count source
DROP VIEW IF EXISTS inventory.view_physical_count;
CREATE OR REPLACE VIEW inventory.view_physical_count
AS SELECT pc.id,
    pc.physical_count_transaction,
    pc.date_trans,
    pc.department,
    pc.item,
    i.sku,
    i.desc_long,
    big.unit_description AS unit_of_purchase,
    small.unit_description AS unit_of_usage,
    ic.category_description,
    pc.expiration_date,
    pc.on_hand,
    COALESCE(pl.monthly_count, 0::bigint) AS monthly_count,
    pc.variance,
    pc.unit_cost,
    pc.is_posted,
    pc.is_cancel
   FROM inventory.physical_count pc
     LEFT JOIN inventory.plogcountref pl ON pl.physical_count = pc.id
     LEFT JOIN inventory.item i ON pc.item = i.id
     LEFT JOIN inventory.unit_measurements big ON i.unit_of_purchase = big.id
     LEFT JOIN inventory.unit_measurements small ON i.unit_of_usage = small.id
     LEFT JOIN inventory.item_categories ic ON i.item_category = ic.id;