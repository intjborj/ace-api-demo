CREATE TABLE dietary.diet_notes
(
    id uuid NOT NULL,
    "case" uuid,
    "employee" uuid,
    "date_time" date,
    "pes_statement" varchar,
    "dietitian_note" varchar,

    "created_by" varchar(50) COLLATE "pg_catalog"."default",
    "created_date" timestamp(6) DEFAULT now(),
    "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
    "last_modified_date" timestamp(6) DEFAULT now(),
    "deleted" bool,
    PRIMARY KEY (id)
);
