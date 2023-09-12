CREATE SCHEMA "hospital_configuration";

CREATE TABLE "hospital_configuration"."constant_types" (
  "id" uuid NOT NULL,
  "name" varchar(255),
  "description" varchar(255),
  "created_by" varchar COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "last_modified_by" varchar COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6),
  "deleted" bool,
  PRIMARY KEY ("id")
)
;

CREATE TABLE "hospital_configuration"."constants" (
  "id" uuid NOT NULL,
  "name" varchar(255),
  "value" varchar(255),
  "short_code" varchar(255),
  "type" uuid,
  "created_by" varchar COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "last_modified_by" varchar COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6),
  "deleted" bool,
  PRIMARY KEY ("id")
)
;