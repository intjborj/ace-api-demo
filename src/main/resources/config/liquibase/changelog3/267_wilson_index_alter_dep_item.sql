-- inventory.inventory_ledger foreign keys

ALTER TABLE inventory.department_item ADD CONSTRAINT department_item_item_fkey FOREIGN KEY (item) REFERENCES inventory.item(id);
ALTER TABLE inventory.department_item ADD CONSTRAINT department_item_department_fkey FOREIGN KEY (department) REFERENCES departments(id);


----index ---
CREATE INDEX IF NOT EXISTS department_item_item_idx ON inventory.department_item USING btree (department);
CREATE INDEX IF NOT EXISTS department_item_department_idx ON inventory.department_item USING btree (item);