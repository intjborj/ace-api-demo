CREATE TABLE "hospital_configuration"."hosp_opt_discharges_ev" (
  "id" uuid NOT NULL,
  "emergencyvisits" int4,
  "emergencyvisitsadult" int4,
  "emergencyvisitspediatric" int4,
  "evfromfacilitytoanother" int4,
  "reportingyear" int4,


  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);