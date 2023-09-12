ALTER TABLE inventory.quantity_adjustment_type ADD COLUMN source_value varchar default null,
ADD COLUMN reverse bool default false;