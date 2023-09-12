ALTER TABLE billing.billing_item ADD pricing_tier uuid NULL;
ALTER TABLE billing.billing_item ADD CONSTRAINT billing_item_fk FOREIGN KEY (pricing_tier) REFERENCES billing.price_tier_details(id) ON DELETE SET NULL ON UPDATE CASCADE;
