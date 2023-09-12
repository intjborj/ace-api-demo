CREATE TABLE "fixed_assets"."fixed_asset_category" (
  "id" uuid NOT NULL,
  "category_code" varchar(255),
  "category_description" varchar(255),
  "is_active" bool,

  "deleted" bool,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP
);
