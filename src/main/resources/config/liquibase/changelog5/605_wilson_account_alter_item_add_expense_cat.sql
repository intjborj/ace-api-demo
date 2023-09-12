ALTER TABLE inventory.accounting_categories ADD COLUMN account_type varchar default null;
ALTER TABLE inventory.item ADD COLUMN accounting_expense_category uuid default null;