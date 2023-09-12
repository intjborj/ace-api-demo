CREATE TABLE "hospital_configuration"."gen_info_classification" (
  "id" uuid NOT NULL,
  "servicecapability" int4,
  "general" int4,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);