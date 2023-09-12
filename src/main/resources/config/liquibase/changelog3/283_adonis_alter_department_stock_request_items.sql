ALTER TABLE inventory.department_stock_request_items ADD status varchar, ADD stock_issue_items uuid;
ALTER TABLE inventory.department_stock_request ADD stock_issue uuid;
ALTER TABLE inventory.stock_issue ADD request_no varchar;

