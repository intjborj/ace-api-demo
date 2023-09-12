DROP TABLE IF EXISTS inventory.beginning_items;
DROP TABLE IF EXISTS inventory.beginning_transactions;
CREATE TABLE inventory.beginning_transactions (
	id                  uuid PRIMARY KEY NOT NULL,
	trans_no            varchar,
	trans_date          date,
	department          uuid,
	remarks             varchar,
	trans_by            uuid,
	status              varchar,
	acct_type           uuid,
	posted_ledger       uuid,
	posted              bool,
	posted_by           varchar,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	deleted             bool,

	foreign key (department) references public.departments(id)
);

CREATE TABLE inventory.beginning_items (
	id                  uuid PRIMARY KEY NOT NULL,
	beginning           uuid,
	item                uuid,
	department          uuid,
	qty                 int,
	unit_cost           numeric,
	posted              bool,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	deleted             bool,

	foreign key (beginning) references inventory.beginning_transactions(id),
	foreign key (department) references public.departments(id),
	foreign key (item) references inventory.item(id)
);