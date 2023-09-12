CREATE TABLE "hospital_configuration"."hosp_opt_discharges_number_deliveries" (
  "id" uuid NOT NULL,
  "totalifdelivery" int4,
  "totallbvdelivery" int4,
  "totallbcdelivery" int4,
  "totalotherdelivery" int4,
  "reportingyear" int4,


  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);