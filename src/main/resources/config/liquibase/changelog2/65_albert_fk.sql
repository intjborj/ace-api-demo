ALTER TABLE billing.billing_item
    ADD CONSTRAINT billing_package_fk FOREIGN KEY (package_id) REFERENCES billing.package(id) ON DELETE SET NULL ON UPDATE CASCADE;
