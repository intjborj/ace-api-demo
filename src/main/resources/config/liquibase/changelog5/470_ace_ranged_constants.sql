CREATE TABLE "hospital_configuration"."ranged_constants" (
	"id" uuid NOT NULL,
	"field_name" varchar,
	"range_from" numeric,
	"range_to" numeric,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
	"deleted" bool,
	PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE
)