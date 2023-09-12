ALTER TABLE hospital_configuration.comlogik_settings
  ADD COLUMN "deleted" bool,
  ADD COLUMN "created_by" varchar(50) COLLATE "default",
  ADD COLUMN "created_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
  ADD COLUMN "last_modified_by" varchar(50) COLLATE "default",
  ADD COLUMN "last_modified_date" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP;