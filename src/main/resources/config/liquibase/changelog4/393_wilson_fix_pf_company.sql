CREATE OR REPLACE FUNCTION accounting.get_sum_credit_memo(ar_item uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE memo numeric(10, 2);
BEGIN
		memo = (
            select coalesce(round(sum(atd.amount),2),0) as credit_memo from accounting.ar_transaction_details atd
			where atd.account_receivable_item_id = ar_item and atd."type" = 'memo'
		);
		RETURN memo;
END; $function$
;

CREATE OR REPLACE FUNCTION accounting.get_sum_payments(ar_item uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE payments numeric(10, 2);
BEGIN
		payments = (
            select coalesce(round(sum(atd.amount),2),0) as payments from accounting.ar_transaction_details atd
			where atd.account_receivable_item_id = ar_item and atd."type" = 'payments'
		);
		RETURN payments;
END; $function$
;


DROP VIEW IF EXISTS accounting.pf_company;

CREATE OR REPLACE VIEW accounting.pf_company
AS SELECT ari.id,
ari.account_receivable_id ,
ari."type",
ari.status,
ari.description as patient,
ari.debit as pf_fee,
COALESCE(( select accounting.get_sum_payments(ari.id)), 0) as total_payments,
ari.amount,
ari.discount,
COALESCE(( select accounting.get_sum_credit_memo(ari.id)), 0) AS credit_memo,
COALESCE(( select accounting.get_sum_payments(ari.id)), 0) AS pf_payable,
COALESCE(ari.debit - ((select accounting.get_sum_payments(ari.id)) + (select accounting.get_sum_credit_memo(ari.id))), 0) as balance,
ari.cwt,
ari.trans_type,
arid.field_value::uuid as billing_item,
bi.description as doctor,
arid2.field_value::uuid as emp_id,
s.id as supplier_id,
ari.ap_process
   FROM accounting.account_receivable_items ari
     LEFT JOIN accounting.account_receivable_items_details arid ON arid.account_receivable_items = ari.id and arid.field_name = 'BILLING_ITEM_ID'
     LEFT JOIN accounting.account_receivable_items_details arid2 ON arid2.account_receivable_items = ari.id and arid2.field_name = 'PF_EMPLOYEEID'
     left join inventory.supplier s ON s.employee_id = arid2.field_value::uuid
     left join billing.billing_item bi on bi.id = arid.field_value::uuid
  WHERE ari."type" = 'PF';