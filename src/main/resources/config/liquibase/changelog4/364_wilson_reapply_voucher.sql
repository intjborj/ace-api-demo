ALTER TABLE accounting.disbursement ADD COLUMN reapplication uuid default null;
CREATE TABLE accounting.reapplication (
    id                  uuid NOT NULL primary key,
	transaction_type    uuid NULL,
	supplier            uuid NULL,
	disbursement        uuid null,
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

ALTER TABLE accounting.disbursement_ap ADD COLUMN reapplication uuid default null;
