DROP TABLE IF EXISTS "accounting"."transaction_type";
DROP TABLE IF EXISTS "accounting"."account_config";
DROP TABLE IF EXISTS "accounting"."account_list";

CREATE TABLE "accounting"."transaction_type" (
	id                      uuid not null primary key,
	description             varchar,
	tag                     varchar,

	created_by                     varchar(50) NULL,
    created_date                   timestamp NULL DEFAULT now(),
    last_modified_by               varchar(50) NULL,
    last_modified_date             timestamp NULL DEFAULT now(),
    deleted                        bool

);

CREATE TABLE "accounting"."account_config" (
	id                      uuid not null primary key,
	trans_type              uuid,
	doc_type                varchar,
	book                    varchar,

    created_by                     varchar(50) NULL,
    created_date                   timestamp NULL DEFAULT now(),
    last_modified_by               varchar(50) NULL,
    last_modified_date             timestamp NULL DEFAULT now(),
    deleted                        bool,
    FOREIGN KEY (trans_type) REFERENCES  accounting.transaction_type(id)
);

CREATE TABLE "accounting"."account_list" (
	id                      uuid not null primary key,
	parent                  uuid,
	debit_account           uuid,
	debit_tag               varchar,
	credit_account          uuid,
	credit_tag              varchar,
	status                  int, -- 1: primary account, 2: compound account

    created_by                     varchar(50) NULL,
    created_date                   timestamp NULL DEFAULT now(),
    last_modified_by               varchar(50) NULL,
    last_modified_date             timestamp NULL DEFAULT now(),
    deleted                        bool,
    FOREIGN KEY (parent) REFERENCES  accounting.account_config(id),
    FOREIGN KEY (debit_account) REFERENCES accounting.chart_of_accounts(id),
    FOREIGN KEY (credit_account) REFERENCES accounting.chart_of_accounts(id)
);

