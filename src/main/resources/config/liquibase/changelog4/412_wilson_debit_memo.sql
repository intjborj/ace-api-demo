CREATE TABLE accounting.debit_memo (
    id                  uuid NOT NULL primary key,
	transaction_type    uuid NULL,
	supplier            uuid NULL,
	debit_no            varchar null,
	debit_date          date,
	debit_type          varchar null,
	memo_amount         numeric default 0,
	ewt_amount          numeric default 0,
	discount            numeric default 0,
	net_amount          numeric default 0,
	remarks             varchar NULL,
	status              varchar default 'DRAFT',
	posted              bool default false,
	posted_ledger       uuid null,
	posted_by           varchar null,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (transaction_type) references accounting.ap_trans_types(id),
	foreign key (supplier) references inventory.supplier(id)
);