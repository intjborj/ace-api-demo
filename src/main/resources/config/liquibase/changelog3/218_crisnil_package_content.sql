create table ancillary.package_content
(
	id uuid,
	parent_id uuid,
	item_id uuid,
	item_name varchar(100),
	qty integer,
	inventoriable boolean,
	deleted boolean,
	created_by varchar(100),
	created_date timestamp,
	last_modified_by varchar(100),
	last_modified_date timestamp
);

