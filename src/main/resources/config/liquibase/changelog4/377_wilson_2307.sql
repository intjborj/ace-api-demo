DROP TABLE IF EXISTS accounting.wtx_2307;
CREATE TABLE accounting.wtx_2307 (
    id                  uuid NOT NULL primary key,
	ref_id              uuid NULL,
	ref_no              varchar null,
	type                varchar null,
	wtx_date            date,
	supplier            uuid null,
	gross               numeric default 0,
	vat_amount          numeric default 0,
	net_vat             numeric default 0,
	ewt_amount          numeric default 0,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (supplier) references inventory.supplier(id)
);
