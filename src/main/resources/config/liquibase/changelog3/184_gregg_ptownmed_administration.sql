CREATE TABLE "pms"."patient_own_medicine_administration" (
	"id" uuid NOT NULL,
	"patient_own_medicine" uuid,
	"action" varchar(50) COLLATE "default",
	"dose" varchar(50) COLLATE "default",
	"remarks" text COLLATE "default",
	"entry_datetime" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"employee" uuid,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)
WITH (OIDS=FALSE);