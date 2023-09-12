ALTER TABLE cashiering.payment_tracker
ADD COLUMN IF NOT EXISTS payor_name varchar,
ADD COLUMN IF NOT EXISTS transaction_category varchar,
ADD COLUMN IF NOT EXISTS transaction_category_id uuid,
ADD COLUMN IF NOT EXISTS patient_id uuid,
ADD COLUMN IF NOT EXISTS employee_id uuid,
ADD COLUMN IF NOT EXISTS supplier_id uuid,
ADD COLUMN IF NOT EXISTS company_id uuid;

CREATE INDEX IF NOT EXISTS idx_payment_tracker_transaction_category_id ON cashiering.payment_tracker (transaction_category_id);
CREATE INDEX IF NOT EXISTS idx_payment_tracker_patient_id ON cashiering.payment_tracker (patient_id);
CREATE INDEX IF NOT EXISTS idx_payment_tracker_employee_id ON cashiering.payment_tracker (employee_id);
CREATE INDEX IF NOT EXISTS idx_payment_tracker_supplier_id ON cashiering.payment_tracker (supplier_id);
CREATE INDEX IF NOT EXISTS idx_payment_tracker_company_id ON cashiering.payment_tracker (company_id);

ALTER TABLE cashiering.payment_transaction_type
ADD COLUMN IF NOT EXISTS misc_type varchar;
