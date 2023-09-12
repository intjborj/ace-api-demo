CREATE TABLE "hospital_configuration"."pharmacy_configuration" (
	"id" uuid NOT NULL,
	"sticker_printer_location" varchar,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);