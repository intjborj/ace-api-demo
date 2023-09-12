
ALTER TABLE billing.billing_item ADD package_id uuid NULL;
ALTER TABLE billing.billing_item ADD posted boolean NULL;
ALTER TABLE billing.billing_item ADD for_posting boolean NULL;

