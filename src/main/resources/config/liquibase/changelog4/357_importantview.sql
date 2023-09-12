CREATE OR REPLACE VIEW accounting.header_ledger_entityname_and_reference_not_approved
AS SELECT hl.entity_name,
          hl.invoice_soa_reference
   FROM accounting.header_ledger hl
   WHERE hl.approved_datetime IS NULL;

CREATE OR REPLACE VIEW accounting.header_ledger_entityname_and_reference_approved
AS SELECT hl.entity_name,
          hl.invoice_soa_reference
   FROM accounting.header_ledger hl
   WHERE hl.approved_datetime IS NOT NULL;