CREATE TABLE "inventory"."material_production" (
  "id" uuid NOT NULL,
  "mp_no" varchar,
  "item" uuid,
    "quantity" numeric,
  "description" varchar(255),
  "unit_cost" numeric,
  "deleted" bool,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;

CREATE TABLE "inventory"."material_production_item" (
  "id" uuid NOT NULL,
  "material_production" uuid,
  "item" uuid,
  "qty" int4,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "deleted" bool
)
;

