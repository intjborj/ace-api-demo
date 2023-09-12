CREATE TABLE accounting.ap_trans_types (
    id                  uuid NOT NULL primary key,
    supplier_type       uuid,
    description         varchar,
    status              bool,

    created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (supplier_type) references inventory.supplier_types(id)
);


CREATE TABLE accounting.payables (
	id                  uuid NOT NULL primary key,
	receiving           uuid NULL,
	ap_no               varchar NULL,
	supplier            uuid NULL,
	ap_category         varchar null,
	payment_terms       uuid NULL,
	apv_date            date,
	invoice_no          varchar NULL,

	gross_amount        numeric,
	discount_amount     numeric,
	net_of_discount     numeric,
	vat_rate            numeric,
	vat_inclusive       bool,
	vat_amount          numeric,
	net_of_vat          numeric,
	ewt_amount          numeric,
	net_amount          numeric,

	status              varchar,
	posted              bool,
	posted_by           varchar,
	remarks_notes       varchar,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

	foreign key (receiving) references inventory.receiving_report(id),
	foreign key (supplier) references inventory.supplier(id),
	foreign key (payment_terms) references inventory.payment_terms(id)
);

CREATE TABLE accounting.payables_detials (
	id                  uuid NOT NULL primary key,
	payables            uuid,
	trans_type          uuid,
	department          uuid,


	amount              numeric,
	disc_rate           numeric,
	disc_amount         numeric,
	vat_inclusive       bool,
	vat_amount          numeric,
	tax_description     varchar,
	ewt_rate            numeric,
	ewt_amount          numeric,
	net_amount          numeric,

	remarks_notes       varchar,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL,

    foreign key (payables) references accounting.payables(id),
    foreign key (trans_type) references accounting.ap_trans_types(id),
    foreign key (department) references public.departments(id)
);


