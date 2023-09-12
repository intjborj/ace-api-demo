CREATE TABLE "hrm"."employee_leave_request" (
  "id" uuid NOT NULL,
  "requested_by" uuid,
  "approved_by" uuid,
  "status" varchar(255),
  "reason" varchar(255),
  "leave_type" uuid,
  "start_datetime" timestamp(0),
  "end_datetime" timestamp(0),
  "deleted" bool,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;


CREATE TABLE "hrm"."employee_overtime_request" (
  "id" uuid NOT NULL,
  "requested_by" uuid,
  "approved_by" uuid,
  "status" varchar(255),
  "reason" varchar(255),
  "overtime_type" uuid,
  "start_datetime" timestamp(0),
  "end_datetime" timestamp(0),
  "deleted" bool,
  "created_by" varchar(50) COLLATE "pg_catalog"."default",
  "created_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP,
  "last_modified_by" varchar(50) COLLATE "pg_catalog"."default",
  "last_modified_date" timestamp(6) DEFAULT CURRENT_TIMESTAMP
)
;
