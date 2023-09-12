alter table inventory.purchase_order
	add column is_completed bool default false;

alter table inventory.purchase_order_items
    add column qty_in_small int default 0;

alter table inventory.purchase_order_items
    add column delivery_status int default 0; -- 0: for delivery 1: partial delivery 2: completed

alter table inventory.purchase_order_items
    add column delivery_balance int default 0;