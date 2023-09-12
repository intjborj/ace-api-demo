DROP TABLE IF EXISTS accounting.reapplication;
CREATE TABLE accounting.reapplication (
    id                  uuid NOT NULL primary key,
	transaction_type    uuid NULL,
	supplier            uuid NULL,
	disbursement        uuid null,
	discount_amount     numeric default 0,
	ewt_amount          numeric default 0,
	applied_amount      numeric default 0,
	is_posted           bool default false,
	posted_ledger       uuid null,
	status              varchar null,
	remarks             varchar NULL, -- check or cash


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (supplier) references inventory.supplier(id),
	foreign key (transaction_type) references accounting.ap_trans_types(id),
	foreign key (disbursement) references accounting.disbursement(id)
);
