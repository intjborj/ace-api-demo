CREATE TABLE "hospital_configuration"."get_info_quality_management" (
  "id" uuid NOT NULL,
  "qualitymgmttype" int4,
  "description" varchar(255),
  "certifyingbody" varchar(250),
  "philhealthaccreditation" int4,
  "validityfrom" date,
  "validityto" date,
  "reportingyear" int4,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT now(),
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT now(),
  "deleted" bool,
  PRIMARY KEY ("id")
);