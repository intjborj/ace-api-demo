DROP VIEW IF EXISTS accounting.pf_non_company;
CREATE OR REPLACE VIEW accounting.pf_non_company
AS select
bi.id,
bi.billing,
bi.record_no,
bi.description,
bi.department,
bi.transaction_date,
bi.credit as pf_fee,
bid2.field_value as or_number,
bid.field_value::uuid as emp_id,
bid3.field_value::uuid as payment_tracker,
s.id AS supplier_id,
bi.ap_process
from billing.billing_item bi
LEFT JOIN billing.billing_item_details bid2 ON bid2.billingitem = bi.id AND bid2.field_name = 'ORNUMBER'
LEFT JOIN billing.billing_item_details bid3 ON bid3.billingitem = bi.id AND bid3.field_name = 'PAYTRACKER_ID'
INNER JOIN billing.billing_item_details bid ON bid.billingitem = bi.id AND bid.field_name = 'PF_EMPLOYEEID'
LEFT JOIN inventory.supplier s ON s.employee_id = bid.field_value::uuid
where bi.item_type ='PAYMENTS' and bi.status = 'ACTIVE';