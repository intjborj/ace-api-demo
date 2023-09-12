CREATE TABLE "hospital_configuration"."operational_configuration" (
	"id" uuid NOT NULL,
	"order_posting_empty" bool,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);