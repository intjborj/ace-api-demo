CREATE TABLE IF NOT EXISTS "ancillary"."panel_content" (
  "id" uuid NOT NULL,
  "parent" uuid,
  "process_code" varchar,
  "deleted" bool,
  "created_by" varchar,
  "created_date" timestamp(0),
  "last_modified_by" varchar,
  "last_modified_date" timestamp(0),
  "servicename" varchar,
  PRIMARY KEY ("id"),
  CONSTRAINT "services" FOREIGN KEY ("parent") REFERENCES "ancillary"."services" ("id")
)
