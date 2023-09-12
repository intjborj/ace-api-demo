CREATE TABLE dietary.patient_diet_log
(
    id uuid NOT NULL,
    "case" uuid,
    employee uuid,
    "created_by" varchar(50) COLLATE "pg_catalog"."default",
    "created_date" timestamp(6) DEFAULT now(),
    reason character varying,
    "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
    "last_modified_date" timestamp(6) DEFAULT now(),
    "deleted" bool,
    PRIMARY KEY (id)
);
