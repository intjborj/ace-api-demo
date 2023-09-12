DROP TABLE IF EXISTS eclaims.case_reference;
CREATE TABLE eclaims.case_reference (
    id                  uuid NOT NULL primary key,
    pt_case             uuid,
    ext_eligibility     uuid,
	reference_id        varchar(50) NULL,

	created_by          varchar(50) NULL,
	created_date        timestamp NULL DEFAULT now(),
	last_modified_by    varchar(50) NULL,
	last_modified_date  timestamp NULL DEFAULT now(),
	deleted             bool NULL
);

ALTER TABLE "eclaims"."eclaims_accounts"
    ADD CONSTRAINT "fk_ea_employee"
    FOREIGN KEY ("employee")
    REFERENCES "hrm"."employees" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "eclaims"."eclaims_accounts"
    ADD CONSTRAINT "fk_ea_integration"
    FOREIGN KEY ("integration_id")
    REFERENCES "eclaims"."eclaims_settings" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

ALTER TABLE "eclaims"."case_reference"
    ADD CONSTRAINT "fk_cr_case"
    FOREIGN KEY ("pt_case")
    REFERENCES "pms"."cases" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

