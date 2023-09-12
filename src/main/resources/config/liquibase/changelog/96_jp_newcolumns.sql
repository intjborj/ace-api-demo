ALTER TABLE "hrm"."employees"
  ADD COLUMN "zip_code" varchar(25),
  ADD COLUMN "address_2" varchar(255),
  ADD COLUMN "employee_tel_no" varchar(20),
  ADD COLUMN "employee_cel_no" varchar(20),
  ADD COLUMN "philhealth_no" varchar(25),
  ADD COLUMN "sss_no" varchar(25),
  ADD COLUMN "tin_no" varchar(25),
  ADD COLUMN "blood_type" varchar(5),
  ADD COLUMN "basic_salary" numeric,
  ADD COLUMN "pay_freq" varchar(25),
  ADD COLUMN "schedule_type" varchar(25),
  ADD COLUMN "position_type" varchar(255);