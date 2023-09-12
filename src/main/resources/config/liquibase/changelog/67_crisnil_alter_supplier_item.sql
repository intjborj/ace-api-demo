alter table inventory.supplier_item
	add new_column int;

alter table inventory.supplier_item
	add created_by varchar;

alter table inventory.supplier_item
	add created_date timestamp;

alter table inventory.supplier_item
	add last_modified_by varchar;

alter table inventory.supplier_item
	add last_modified_date timestamp;
