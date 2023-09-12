CREATE TABLE accounting.petty_cash (
    id                  uuid NOT NULL primary key,
	transaction_type    uuid NULL,
	payee_name          varchar null,
	pcv_no              varchar null,
	pcv_date            date,
	amount_issued       numeric default 0,
	amount_used         numeric default 0,
	amount_unused       numeric default 0,
	remarks             varchar NULL, -- check or cash
	status              varchar default 'DRAFT',
	posted              bool default false,
	posted_ledger       uuid null,
	posted_by           varchar null,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (transaction_type) references accounting.ap_trans_types(id)
);

CREATE TABLE accounting.expense_trans_type (
    id                  uuid NOT NULL primary key,
	description         varchar NULL,
	type                varchar null,
	source              varchar NULL,
	is_active           bool default false,
	remarks             varchar NULL, -- check or cash


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);

CREATE TABLE accounting.petty_cash_others (
    id                  uuid NOT NULL primary key,
	petty_cash          uuid NULL,
	trans_type          uuid NULL,
	amount              uuid null,
	department          uuid null,
	remarks             varchar NULL, -- check or cash


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (petty_cash) references accounting.petty_cash(id),
	foreign key (trans_type) references accounting.expense_trans_type(id),
	foreign key (department) references public.departments(id)

);

CREATE TABLE accounting.petty_cash_purchases (
    id                  uuid NOT NULL primary key,
	item                uuid NULL,
	department          uuid NULL,
	petty_cash          uuid NULL,
	qty                 int default 0,
	unit_cost           numeric default 0,
	disc_rate           numeric default 0,
	disc_amount         numeric default 0,
	net_amount          numeric default 0,
	is_vat              bool default false,
	vat_amount          numeric default 0,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (item) references inventory.item(id),
	foreign key (petty_cash) references accounting.petty_cash(id),
	foreign key (department) references public.departments(id)
);

