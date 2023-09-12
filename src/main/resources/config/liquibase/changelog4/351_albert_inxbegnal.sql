CREATE INDEX header_ledger_beginning_balance_idx ON accounting.header_ledger (beginning_balance);


CREATE OR REPLACE VIEW accounting.beginning_balance_ledger
AS select l.* from accounting.ledger l
                       left join accounting.header_ledger hl  on hl.id=l."header"
   where hl.beginning_balance = true;
