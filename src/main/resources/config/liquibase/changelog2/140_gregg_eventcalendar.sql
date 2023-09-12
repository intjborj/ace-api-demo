DROP TABLE IF EXISTS "hrm"."event_calendar";
CREATE TABLE "hrm"."event_calendar" (
	"id" uuid NOT NULL,
	"name" varchar COLLATE "default",
	"start_date" timestamp(6) NULL,
	"end_date" timestamp(6) NULL,
	"deleted" bool,
	"fixed" varchar COLLATE "default",
	"holiday_type" varchar COLLATE "default",
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT now(),
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT now()
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Primary key structure for table event_calendar
-- ----------------------------
ALTER TABLE "hrm"."event_calendar" ADD PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

