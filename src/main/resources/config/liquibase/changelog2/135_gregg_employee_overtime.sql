DROP TABLE IF EXISTS "hrm"."employee_overtime";
CREATE TABLE "hrm"."employee_overtime" (
	"id" uuid NOT NULL,
	"employee" uuid NOT NULL,
	"overtime_reason" varchar COLLATE "default",
	"overtime_start" timestamp(6) NULL,
	"approved_by" uuid,
	"status" varchar COLLATE "default",
	"overtime_end" timestamp(6) NULL,
	"deleted" bool,
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT now(),
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT now(),
	"approved_date" timestamp(6) NULL,
	"payslip" uuid
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Primary key structure for table employee_overtime
-- ----------------------------
ALTER TABLE "hrm"."employee_overtime" ADD PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;

