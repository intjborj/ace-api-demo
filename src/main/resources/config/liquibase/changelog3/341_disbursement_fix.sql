DROP TABLE IF EXISTS accounting.disbursement;

CREATE TABLE accounting.disbursement (
	id                  uuid NOT NULL primary key,
	payee_name          varchar NULL,
	dis_no              varchar NULL, -- disbursement no
	supplier            uuid NULL,
	trans_type          uuid NULL,
	payment_cat         varchar null,
	dis_type            varchar NULL, -- check or cash
	dis_date            date,

	cash                numeric,
	checks              numeric,
	other_credits       numeric,
	discount_amount     numeric,
	ewt_amount          numeric,
	voucher_amount      numeric,
	applied_amount      numeric,

	status              varchar,
	is_advance          bool,
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

DROP TABLE IF EXISTS accounting.disbursement_check;
CREATE TABLE accounting.disbursement_check (
    id                  uuid NOT NULL primary key,
    --check details this are null if cash voucher is selected
    bank                uuid,
    bank_branch         varchar,
    check_date          date,
    check_no            varchar,
    amount              numeric,
    --end check details

    created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

    foreign key (bank) references accounting.bankaccounts(id)
);

DROP TABLE IF EXISTS accounting.disbursement_ap;
CREATE TABLE accounting.disbursement_ap (
    id                  uuid NOT NULL primary key,
    payable             uuid,
    applied_amount      numeric,
    ewt_desc            varchar,
    ewt_rate            numeric,
    ewt_amount          numeric,
    gross_amount        numeric,
    discount            numeric,
    net_amount          numeric,

    created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

    foreign key (payable) references accounting.payables(id)
);