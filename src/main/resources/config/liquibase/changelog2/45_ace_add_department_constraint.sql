ALTER TABLE "billing"."price_tier_details"
    DROP COLUMN "department",
    ADD COLUMN "department" uuid;

ALTER TABLE "billing"."price_tier_details"
    ADD CONSTRAINT "fk_price_tier_details_department"
    FOREIGN KEY ("department")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;