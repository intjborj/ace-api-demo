CREATE OR REPLACE VIEW accounting.ledger_date
AS SELECT l.id,
          l.credit,
          l.debit,
          l.particulars,
          l.header,
          l.created_by,
          l.created_date,
          l.last_modified_by,
          l.last_modified_date,
          l.journal_account,
          hl.transaction_date,
          hl.fiscal
   FROM accounting.ledger l
            LEFT JOIN accounting.header_ledger hl ON hl.id = l.header;
