CREATE OR REPLACE VIEW accounting.header_ledger_entityname_and_reference_approved
AS SELECT hl.entity_name,
    hl.invoice_soa_reference,
    hl.transaction_date_only,
    hl.journal_type
   FROM accounting.header_ledger hl
WHERE hl.approved_datetime IS NOT NULL;

CREATE OR REPLACE VIEW accounting.header_ledger_entityname_and_reference_not_approved
AS SELECT hl.entity_name,
  hl.invoice_soa_reference,
  hl.transaction_date_only,
  hl.journal_type
 FROM accounting.header_ledger hl
WHERE hl.approved_datetime IS NULL;