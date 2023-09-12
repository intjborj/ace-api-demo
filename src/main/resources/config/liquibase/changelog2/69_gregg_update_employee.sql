ALTER TABLE "hrm"."employees"
	ADD COLUMN "include_in_payroll" bool,
	ADD COLUMN "biometric_device_id" varchar,
	ADD COLUMN "monthly_basic_salary" numeric;