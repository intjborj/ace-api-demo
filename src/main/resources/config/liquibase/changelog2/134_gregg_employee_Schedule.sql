DROP TABLE IF EXISTS "hrm"."employee_schedule";
CREATE TABLE "hrm"."employee_schedule" (
	"id" uuid NOT NULL,
	"employee" uuid NOT NULL,
	"date_time_start" timestamp(6) NULL,
	"date_time_end" timestamp(6) NULL,
	"is_rest_day" bool,
	"is_overtime" bool,
	"deleted" bool,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT now(),
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT now(),
	"meal_break_start" timestamp(6) NULL,
	"meal_break_end" timestamp(6) NULL
)
WITH (OIDS=FALSE);
-- ----------------------------
--  Primary key structure for table employee_daily_schedule
-- ----------------------------
ALTER TABLE "hrm"."employee_schedule" ADD PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Indexes structure for table employee_daily_schedule
-- ----------------------------
CREATE INDEX  "employee_daily_schedule_idx" ON "hrm"."employee_schedule" USING btree(employee "pg_catalog"."uuid_ops" ASC NULLS LAST, date_time_start "pg_catalog"."timestamp_ops" ASC NULLS LAST, date_time_end "pg_catalog"."timestamp_ops" ASC NULLS LAST, meal_break_start "pg_catalog"."timestamp_ops" ASC NULLS LAST, meal_break_end "pg_catalog"."timestamp_ops" ASC NULLS LAST);

