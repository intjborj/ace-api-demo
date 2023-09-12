CREATE TABLE pms.patient_philhealth_data
(
    id uuid NOT NULL,
    "case" uuid NOT NULL,
    member_lastname character varying,
    member_firstname character varying,
    member_middlename character varying,
    member_suffix character varying,
    member_relation character varying,
    member_dob timestamp without time zone,
    member_pin character varying,
    member_type character varying,
    member_civil_status character varying,
      "deleted" bool,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);
