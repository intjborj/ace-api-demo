ALTER TABLE "hrm"."employees"
    ADD COLUMN "department_of_duty" uuid;

ALTER TABLE "hrm"."employees"
    ADD CONSTRAINT "fk_employees_department_of_duty"
    FOREIGN KEY ("department_of_duty")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;

alter table departments
	add column group_category varchar;