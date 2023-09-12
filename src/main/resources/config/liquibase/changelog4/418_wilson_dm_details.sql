CREATE TABLE accounting.debit_memo_details (
    id                  uuid NOT NULL primary key,
	trans_type          uuid NULL,
	debit_memo          uuid NULL,
	department          uuid null,
	type                varchar null,
	percent             numeric default 0,
	amount              numeric default 0,
    remarks             varchar null,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (trans_type) references accounting.expense_trans_type(id),
	foreign key (debit_memo) references accounting.debit_memo(id),
	foreign key (department) references public.departments(id)
)