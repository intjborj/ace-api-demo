ALTER TABLE "billing"."billing_item"
    ADD COLUMN "department" uuid;

ALTER TABLE "billing"."billing_item"
    ADD CONSTRAINT "fk_billing_item_department"
    FOREIGN KEY ("department")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;