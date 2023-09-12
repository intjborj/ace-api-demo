DROP TABLE IF EXISTS eclaims.eclaims_accounts;
CREATE TABLE eclaims.eclaims_accounts (
    id                  uuid NOT NULL primary key,
    employee            uuid,
    integration_id      uuid,
	username            varchar(50) NULL,
	password            varchar(255) NULL,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);

DROP TABLE IF EXISTS eclaims.eclaims_settings;
CREATE TABLE eclaims.eclaims_settings (
    id                  uuid NOT NULL primary key,
    host                varchar(50) NULL,
	provider            varchar(50) NULL,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);

