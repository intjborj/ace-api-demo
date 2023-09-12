alter table eclaims.case_reference
	ADD COLUMN "ext_cf4_id" uuid NULL;

alter table eclaims.case_reference
	ADD COLUMN "cf4_details" text NULL;