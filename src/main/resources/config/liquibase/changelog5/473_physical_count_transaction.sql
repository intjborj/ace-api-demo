DROP TABLE IF EXISTS inventory.physical_count_transactions;
CREATE TABLE inventory.physical_count_transactions (
	id                  uuid NOT NULL PRIMARY KEY,
	trans_no            varchar NULL,
	trans_date          date NULL,
	department          uuid NULL,
	remarks             varchar NULL,
	trans_by            uuid NULL,
	status              varchar NULL,
	acct_type           uuid NULL,
	posted_ledger       uuid NULL,
	posted              bool NULL,
	posted_by           varchar NULL,
	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	deleted             bool NULL,
	foreign key (department) references public.departments(id)
);