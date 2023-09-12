ALTER TABLE inventory.stock_request_item ADD COLUMN no_stock bool default false,
ADD COLUMN cancelled bool default false;