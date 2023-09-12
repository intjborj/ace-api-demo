ALTER TABLE "ancillary"."diagnostic_results"
ALTER COLUMN "parsed" TYPE varchar COLLATE "default",
ADD COLUMN "created_by" varchar,
ADD COLUMN "created_date" timestamp,
ADD COLUMN "last_modified_by" varchar,
ADD COLUMN "last_modified_date" timestamp;
