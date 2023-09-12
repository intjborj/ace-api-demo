ALTER TABLE "billing"."price_tier_details"
    DROP COLUMN "for_senior",
    ADD COLUMN "for_senior" bool default false;