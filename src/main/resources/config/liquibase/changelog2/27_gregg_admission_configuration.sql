CREATE TABLE "hospital_configuration"."admission_configuration" (
	"id" uuid NOT NULL,
	"wristband_printer_location" varchar,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);