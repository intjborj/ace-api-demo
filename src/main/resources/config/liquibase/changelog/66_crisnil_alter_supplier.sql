alter table inventory.supplier
	add created_by varchar;

alter table inventory.supplier
	add created_date timestamp;

alter table inventory.supplier
	add last_modified_by varchar;

alter table inventory.supplier
	add last_modified_date timestamp;

