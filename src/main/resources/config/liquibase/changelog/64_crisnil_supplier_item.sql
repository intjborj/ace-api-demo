create table inventory.supplier_item
(
	id uuid not null,
	supplier uuid,
	item_id uuid,
	cost numeric
);
