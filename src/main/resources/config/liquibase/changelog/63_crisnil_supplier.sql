create table inventory.supplier
(
	id uuid NOT NULL
		constraint supplier_pk
			primary key,
	supplier_name varchar,
	address varchar,
	contact_person varchar,
	contact_no varchar,
	email varchar,
	supplier_code varchar
);
