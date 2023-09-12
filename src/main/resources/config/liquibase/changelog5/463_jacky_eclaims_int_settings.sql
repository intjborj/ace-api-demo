DROP TABLE IF EXISTS hospital_configuration.eclaims_integration_settings;
CREATE TABLE hospital_configuration.eclaims_integration_settings (
    id                  uuid NOT NULL primary key,
    employee            uuid,
	host                varchar(50) NULL,
	username            varchar(50) NULL,
	password            varchar(255) NULL,


	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);

ALTER TABLE "hospital_configuration"."eclaims_integration_settings"
    ADD CONSTRAINT "fk_eis_employee"
    FOREIGN KEY ("employee")
    REFERENCES "hrm"."employees" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;