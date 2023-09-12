CREATE TABLE "hospital_configuration"."hosp_opt_summary_of_patient" (
  "id" uuid NOT NULL,
  "totalinpatients" int4,
  "totalnewborn" int4,
  "totaldischarges" int4,
  "totalpad" int4,
  "totalibd" int4,
  "totalinpatienttransto" int4,
  "totalinpatienttransfrom" int4,
  "totalpatientsremainging" int4,
  "reportingyear" int4,

  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);