
CREATE OR REPLACE VIEW accounting.ledger_date as
SELECT l.*,
       hl.transaction_date

FROM accounting.ledger l
LEFT JOIN accounting.header_ledger hl ON hl.id = l.header;



CREATE OR REPLACE VIEW accounting.beginning_balance_ledger_date as
SELECT l.*,
       hl.transaction_date

FROM accounting.ledger l
         LEFT JOIN accounting.header_ledger hl ON hl.id = l.header
WHERE hl.beginning_balance = true;