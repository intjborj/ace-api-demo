DROP TABLE IF EXISTS accounting.disbursement_check;
CREATE TABLE accounting.disbursement_check (
    id                  uuid NOT NULL primary key,
    disbursement        uuid,
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

    foreign key (bank) references accounting.bankaccounts(id),
    foreign key (disbursement) references accounting.disbursement(id)
);

DROP TABLE IF EXISTS accounting.disbursement_ap;
CREATE TABLE accounting.disbursement_ap (
    id                  uuid NOT NULL primary key,
    disbursement        uuid,
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

    foreign key (payable) references accounting.payables(id),
    foreign key (disbursement) references accounting.disbursement(id)
);