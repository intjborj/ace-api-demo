ALTER TABLE accounting.ledger ADD journal_account jsonb NULL;
CREATE INDEX ledger_journal_account_idx ON accounting.ledger (journal_account);
