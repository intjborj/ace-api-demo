ALTER TABLE accounting.payables
ADD COLUMN applied_amount numeric default 0,
ADD COLUMN disbursement uuid default null;

CREATE TABLE accounting.disbursement (
	id                  uuid NOT NULL primary key,
	payee_name          varchar NULL,
	dis_no              varchar NULL, -- disbursement no
	supplier            uuid NULL,
	payment_cat         varchar null,
	dis_type            varchar NULL, -- check or cash
	dis_date            date,

	cash                numeric,
	checks              numeric,
	other_credits       numeric,
	ewt_amount          numeric,
	applied_amount      numeric,

	status              varchar,
	posted              bool,
	posted_by           varchar,
	remarks_notes       varchar,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (supplier) references inventory.supplier(id)
);