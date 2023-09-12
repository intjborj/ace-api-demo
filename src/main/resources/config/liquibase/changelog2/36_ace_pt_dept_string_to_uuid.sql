ALTER TABLE billing.price_tier_details
    DROP COLUMN department,
    ADD COLUMN department uuid;