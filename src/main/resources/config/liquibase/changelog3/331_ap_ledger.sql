CREATE TABLE accounting.ap_ledger (
	id                  uuid NOT NULL primary key,
	supplier            uuid NULL,
	ledger_type         varchar null, -- PF, AP, CK, CS
	ledger_date         timestamp NULL DEFAULT now(),
	ref_no              varchar null,
	ref_id              uuid null,

	debit               numeric,
	credit              numeric,
	is_include          bool,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (supplier) references inventory.supplier(id)
);