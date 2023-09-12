CREATE TABLE IF NOT EXISTS "ancillary"."ancillary_config" (
  "id" uuid NOT NULL,
  "entity" varchar,
  "setttings_key" varchar,
  "value" varchar,
  "active" bool
)
;
