CREATE TABLE "hospital_configuration"."staffing_pattern_others" (
  "id" uuid NOT NULL,
  "parent" int4,
  "professiondesignation" varchar(250),
  "specialtyboardcertified" int4,
  "fulltime40permanent" int4,
  "fulltime40contractual" int4,
  "parttimepermanent" int4,
  "parttimecontractual" int4,
  "activerotatingaffiliate" int4,
  "outsoured" int4,
  "reportingyear" int4,



  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);