CREATE TABLE IF NOT EXISTS "ancillary"."integration_config" (
  "id" uuid NOT NULL,
  "demo_mode" bool,
  "nas_location" varchar,
  "carestream_url" varchar,
  "watched_directory" varchar,
  "enable_integration" bool,
  "middleware_ip" varchar,
  "adt_port" varchar,
  "orm_port" varchar,
  PRIMARY KEY ("id")
)
;
