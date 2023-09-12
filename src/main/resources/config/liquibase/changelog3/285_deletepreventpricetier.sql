

ALTER TABLE billing.billing_item DROP CONSTRAINT IF EXISTS billing_item_fk;
ALTER TABLE billing.billing_item ADD CONSTRAINT billing_item_fk FOREIGN KEY (pricing_tier) REFERENCES billing.price_tier_details(id) ON UPDATE CASCADE ON DELETE RESTRICT;
