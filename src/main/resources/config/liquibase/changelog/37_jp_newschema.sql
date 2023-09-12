CREATE SCHEMA "billing";

CREATE TABLE "billing"."billing" (
                                   "id" uuid NOT NULL,
                                   "entry_datetime" timestamp,
                                   "billing_no" varchar,
                                   "status" varchar,
                                   "created_by" varchar(50) COLLATE "pg_catalog"."default",
                                   "created_date" timestamp(6) DEFAULT now(),
                                   "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
                                   "last_modified_date" timestamp(6) DEFAULT now()

)
;

CREATE TABLE "billing"."billing_item" (
                                        "id" uuid NOT NULL,
                                        "billing" uuid,
                                        "record_no" varchar,
                                        "description" varchar,
                                        "qty" decimal,
                                        "status" varchar,
                                        "price" decimal,
                                        "created_by" varchar(50) COLLATE "pg_catalog"."default",
                                        "created_date" timestamp(6) DEFAULT now(),
                                        "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
                                        "last_modified_date" timestamp(6) DEFAULT now()
);

