ALTER TABLE billing.billing ADD CONSTRAINT billing_pk PRIMARY KEY (id);
ALTER TABLE billing.billing_item ADD CONSTRAINT billing_item_pk PRIMARY KEY (id);
ALTER TABLE billing.billing_item ADD debit numeric(15,2) NULL;
ALTER TABLE billing.billing_item ADD credit numeric(15,2) NULL;