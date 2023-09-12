ALTER TABLE "billing"."price_tier_details"
    ADD COLUMN "department" uuid,
    ADD COLUMN "for_senior" bool;

ALTER TABLE "billing"."price_tier_details"
    ADD CONSTRAINT "fk_price_tier_details_department"
    FOREIGN KEY ("department")
    REFERENCES "public"."departments" ("id")
    ON UPDATE CASCADE ON DELETE RESTRICT;


ALTER TABLE "public"."departments"
    ADD COLUMN "has_special_price_tier" bool;