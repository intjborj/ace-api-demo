CREATE TABLE "hospital_configuration"."hosp_operations_minor_opt" (
  "id" uuid NOT NULL,
  "operationcode" varchar(50),
  "surgicaloperation" varchar(255),
  "number" int4,
  "reportingyear" int4,



  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);