CREATE OR REPLACE VIEW accounting.header_ledger_entityname_and_reference_not_approved
AS SELECT hl.entity_name,
    hl.invoice_soa_reference,
    hl.transaction_date_only
   FROM accounting.header_ledger_old hl
  WHERE hl.approved_datetime IS NULL;

CREATE OR REPLACE VIEW accounting.header_ledger_entityname_and_reference_approved
AS SELECT hl.entity_name,
  hl.invoice_soa_reference,
  hl.transaction_date_only
 FROM accounting.header_ledger_old hl
WHERE hl.approved_datetime IS NOT NULL;

CREATE INDEX IF NOT EXISTS payment_tracker_ledger_header_idx ON cashiering.payment_tracker USING btree (ledger_header);