ALTER TABLE billing.billing_item ADD vat_output_tax numeric(15,2) NULL;
update   billing.billing_item  set vat_output_tax = 0.0;
