DROP TABLE IF EXISTS "hrm"."employee_attendance";
CREATE TABLE "hrm"."employee_attendance" (
	"id" uuid NOT NULL,
	"employee" uuid,
	"attendance_time" timestamp(6) NULL,
	"type" varchar COLLATE "default",
	"source" varchar COLLATE "default",
	"deleted" bool,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT now(),
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT now(),
	"additional_note" varchar COLLATE "default",
	"original_attendance_time" timestamp(6) NULL,
	"hide" bool
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Primary key structure for table employee_attendance
-- ----------------------------
ALTER TABLE "hrm"."employee_attendance" ADD PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

-- ----------------------------
--  Indexes structure for table employee_attendance
-- ----------------------------
CREATE INDEX  "employee_attendance_idx" ON "hrm"."employee_attendance" USING btree(employee "pg_catalog"."uuid_ops" ASC NULLS LAST, attendance_time "pg_catalog"."timestamp_ops" ASC NULLS LAST);

