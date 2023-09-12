ALTER TABLE "pms"."cases"
    ADD COLUMN "department" uuid;

ALTER TABLE "pms"."cases"
    ADD CONSTRAINT "fk_cases_department"
    FOREIGN KEY ("department")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;