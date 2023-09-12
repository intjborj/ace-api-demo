DROP TABLE IF EXISTS accounting.petty_cash_others;
CREATE TABLE accounting.petty_cash_others (
    id                  uuid NOT NULL primary key,
	petty_cash          uuid NULL,
	trans_type          uuid NULL,
	department          uuid null,
	amount              numeric default 0,
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