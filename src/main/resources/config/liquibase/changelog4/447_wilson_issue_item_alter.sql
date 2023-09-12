ALTER TABLE inventory.stock_issue_items DROP COLUMN request_no;
ALTER TABLE inventory.stock_issue_items
ADD COLUMN request_item uuid default null;