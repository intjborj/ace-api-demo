CREATE TABLE billing.billingitems_vat_amountdetails (
                                                    id uuid NULL DEFAULT uuid_generate_v4(),
                                                    billingitemsid varchar NULL,
                                                    amount numeric(15,2) NULL,
                                                    billingitem uuid NULL,
                                                    CONSTRAINT fk_billingitems_amountdetails_billingitem FOREIGN KEY (billingitem) REFERENCES billing.billing_item(id) ON UPDATE CASCADE ON DELETE CASCADE
);
CREATE INDEX dx_bivatamt_billingitem ON billing.billingitems_vat_amountdetails USING btree (billingitem);
CREATE INDEX idx_bivatamt_billingitemsid ON billing.billingitems_vat_amountdetails USING btree (billingitemsid);


ALTER TABLE billing.discounts ADD include_vat boolean NULL;
