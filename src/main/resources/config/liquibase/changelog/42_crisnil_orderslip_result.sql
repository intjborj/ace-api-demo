CREATE TABLE IF NOT EXISTS "ancillary"."diagnostic_results" (
"id" uuid NOT NULL,
"orderslip" uuid,
"name" varchar(255),
"type" varchar(255),
"data" varchar(255),
"service" uuid,
"mimetype" varchar(255),
"file_name" varchar(255),
"url_path" varchar(255),
"accession" varchar(255),
"migrated" bool,
"deleted" bool,
"parsed" varchar(255),
CONSTRAINT "orderslip" FOREIGN KEY ("orderslip") REFERENCES "ancillary"."orderslips" ("id")
);

