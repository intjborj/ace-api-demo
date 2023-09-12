ALTER TABLE "hospital_configuration"."eclaims_integration_settings"
  DROP COLUMN "employee",
  DROP COLUMN "username",
  DROP COLUMN "password";

DROP TABLE IF EXISTS hospital_configuration.eclaims_integration_account;
CREATE TABLE hospital_configuration.eclaims_integration_account (
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