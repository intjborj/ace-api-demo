CREATE TABLE "hospital_configuration"."hospital_info" (
  "id" uuid NOT NULL,
  "hospital_name" varchar(255),
  "address" varchar(255),
  "address_line2" varchar(255),
  "city" varchar(255),
  "street" varchar(255),
  "zip" varchar(255),
  "country" varchar(255),
  "tel_no" varchar(255),
  "mobile" varchar(255),
  "other_no" varchar(255),
  "fax" varchar(255),
  "email" varchar(255),
  "service_level" varchar(255),
  "trauma_capable" bool,
  "trauma_receiving" bool,
  "bed_capacity" int4,
  "implemented_bed" int4,
  "iso_validity" varchar(255),
  "international_accreditation" varchar(255),
  "philhealth_accreditation" varchar(255),
  "philhealth_accreditation_validity" varchar(255),
  "pcaho_validity" varchar(255),
    "created_by" varchar COLLATE "pg_catalog"."default",
  "created_date" timestamp(6),
  "last_modified_by" varchar COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6),
  "deleted" bool,
  PRIMARY KEY ("id")
)
;