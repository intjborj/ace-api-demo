alter table inventory.supplier_item
			add deleted bool default false;

ALTER TABLE inventory.supplier_item DROP COLUMN new_column;