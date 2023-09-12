ALTER TABLE "public"."department"
  RENAME TO "departments";

ALTER TABLE "public"."departments"
  RENAME COLUMN "name" TO "department_name";

ALTER TABLE "public"."departments"
  RENAME COLUMN "description" TO "department_desc";

ALTER TABLE "public"."departments"
  ADD COLUMN "department_code" varchar;