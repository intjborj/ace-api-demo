ALTER TABLE billing.billing_item ADD transaction_date timestamp NULL;


update billing.billing_item set transaction_date = created_date where transaction_date is null;
