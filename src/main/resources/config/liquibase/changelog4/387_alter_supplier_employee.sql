ALTER TABLE "inventory"."supplier" ADD COLUMN "employee_id" uuid default null;
ALTER TABLE "hrm"."employees" ADD COLUMN "supplier_id" uuid default null;