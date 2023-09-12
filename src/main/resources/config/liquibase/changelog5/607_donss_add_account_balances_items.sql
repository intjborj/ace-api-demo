CREATE TABLE IF NOT EXISTS accounting.account_balances_item (
	id uuid NOT NULL DEFAULT uuid_generate_v4(),
	ledger_id uuid NULL,
	account_balances_id uuid NULL,
	credit numeric(15, 2) NULL,
	debit numeric(15, 2) NULL,
	"header" uuid NULL,
	journal_account jsonb NULL,
	created_by varchar NULL,
	created_date timestamp NULL DEFAULT CURRENT_TIMESTAMP,
	last_modified_by varchar NULL,
	last_modified_date timestamp NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX ledger_idx ON accounting.account_balances_item USING btree (ledger_id);
CREATE INDEX account_balances_idx ON accounting.account_balances_item USING btree (account_balances_id);
CREATE INDEX header_account_balances_idx ON accounting.account_balances_item USING btree (header);
CREATE INDEX account_balances_items_journal_account_idx ON accounting.account_balances_item USING btree (journal_account);

ALTER TABLE accounting.account_balances_item ADD CONSTRAINT fk_account_balances FOREIGN KEY (account_balances_id) REFERENCES accounting.account_balances(id) ON DELETE CASCADE ON UPDATE CASCADE;