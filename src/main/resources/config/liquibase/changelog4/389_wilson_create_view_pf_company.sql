CREATE OR REPLACE VIEW accounting.pf_company
AS SELECT ari.id,
ari.account_receivable_id ,
ari."type",
ari.status,
ari.description as patient,
ari.debit as pf_fee,
ari.credit as total_payments,
ari.amount,
ari.discount,
(ari.debit - ari.credit) as balance,
ari.cwt,
ari.trans_type,
arid.field_value as billing_item,
bi.description as doctor,
arid2.field_value as emp_id,
s.id as supplier_id,
ari.ap_process
   FROM accounting.account_receivable_items ari
     LEFT JOIN accounting.account_receivable_items_details arid ON arid.account_receivable_items = ari.id and arid.field_name = 'BILLING_ITEM_ID'
     LEFT JOIN accounting.account_receivable_items_details arid2 ON arid2.account_receivable_items = ari.id and arid2.field_name = 'PF_EMPLOYEEID'
     left join inventory.supplier s ON s.employee_id = arid2.field_value::uuid
     left join billing.billing_item bi on bi.id = arid.field_value::uuid
  WHERE ari."type" = 'PF';