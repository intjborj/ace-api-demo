CREATE TABLE "hospital_configuration"."revenues" (
  "id" uuid NOT NULL,
  "amountfromdoh" decimal,
  "amountfromlgu" decimal,
  "amountfromdonor" decimal,
  "amountfromprivateorg" decimal,
  "amountfromphilhealth" decimal,
  "amountfrompatient" decimal,
  "amountfromreimbursement" decimal,
  "amountfromothersources" decimal,
  "grandtotal" decimal,
  "reportingyear" int4,


  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);