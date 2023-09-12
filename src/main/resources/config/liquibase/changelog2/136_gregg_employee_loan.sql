DROP TABLE IF EXISTS "hrm"."employee_loan";
CREATE TABLE "hrm"."employee_loan" (
	"id" uuid NOT NULL,
	"employee" uuid NOT NULL,
	"loan_type" varchar COLLATE "default",
	"loan_amount" numeric,
	"loan_total_payable" numeric,
	"loan_monthly_payable" numeric,
	"note" varchar COLLATE "default",
	"status" varchar COLLATE "default",
	"created_by" varchar(50) COLLATE "default",
	"created_date" timestamp(6) NULL DEFAULT now(),
	"last_modified_by" varchar(50) COLLATE "default",
	"last_modified_date" timestamp(6) NULL DEFAULT now(),
	"deleted" bool
)
WITH (OIDS=FALSE);

-- ----------------------------
--  Primary key structure for table employee_loan
-- ----------------------------
ALTER TABLE "hrm"."employee_loan" ADD PRIMARY KEY ("id") NOT DEFERRABLE INITIALLY IMMEDIATE;