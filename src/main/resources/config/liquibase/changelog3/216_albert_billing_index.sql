CREATE INDEX billing_billing_no_idx ON billing.billing (billing_no);
CREATE INDEX billing_status_idx ON billing.billing (status);
CREATE INDEX billing_patient_idx ON billing.billing (patient);
CREATE INDEX billing_patient_case_idx ON billing.billing (patient_case);



CREATE INDEX billing_item_billing_idx ON billing.billing_item (billing);

