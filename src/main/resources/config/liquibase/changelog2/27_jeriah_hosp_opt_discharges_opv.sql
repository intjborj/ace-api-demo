CREATE TABLE "hospital_configuration"."hosp_opt_discharges_opv" (
  "id" uuid NOT NULL,
  "newpatient" int4,
  "revisit" int4,
  "adult" int4,
  "pediatric" int4,
  "adultgeneralmedicine" int4,
  "specialtynonsurgical" int4,
  "surgical" int4,
  "antenatal" int4,
  "postnatal" int4,
  "	reportingyear" int4,


  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);