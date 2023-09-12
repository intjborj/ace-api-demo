
ALTER TABLE billing.package ADD discount_target uuid NULL;
ALTER TABLE billing.package ADD CONSTRAINT package_fk3 FOREIGN KEY (discount_target) REFERENCES billing.discounts(id);

