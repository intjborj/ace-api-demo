drop table if exists hospital_configuration.gen_info_classification;

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

alter table hospital_configuration.gen_info_classification
    add column specialty int,
    add column specilaty_specify varchar,
    add column trauma_capability int,
    add column nature_of_ownership int,
    add column government int,
    add column "national" int4,
    add column "local" int4,
    add column private int,
    add column reporting_year int;







