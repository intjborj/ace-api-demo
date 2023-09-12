DROP TABLE IF EXISTS accounting.disbursement_exp;
CREATE TABLE accounting.disbursement_exp (
    id                  uuid NOT NULL primary key,
	disbursement        uuid NULL,
	trans_type          uuid NULL,
	department          uuid null,
	amount              numeric default 0,
	remarks             varchar NULL, -- check or cash


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (disbursement) references accounting.disbursement(id),
	foreign key (trans_type) references accounting.expense_trans_type(id),
	foreign key (department) references public.departments(id)

);

DROP TABLE IF EXISTS accounting.disbursement_wtx;
CREATE TABLE accounting.disbursement_wtx (
    id                  uuid NOT NULL primary key,
	disbursement        uuid NULL,
	ewt_desc            varchar NULL,
	ewt_rate            numeric default 0,
	ewt_amount          numeric default 0,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (disbursement) references accounting.disbursement(id)
);