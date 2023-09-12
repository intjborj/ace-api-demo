ALTER TABLE billing.service_price_controls
    ADD COLUMN percentage_value numeric(15,2);

ALTER TABLE billing.price_tier_details
    ALTER COLUMN target_audience SET DEFAULT 'FOR_ALL';

UPDATE billing.price_tier_details SET target_audience = 'FOR_ALL';
UPDATE billing.price_tier_details SET target_audience = 'FOR_SENIORS' WHERE for_senior = true;