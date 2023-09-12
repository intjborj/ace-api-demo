CREATE OR REPLACE FUNCTION accounting.get_sum_credit_memo(ar_item uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE memo numeric(10, 2);
BEGIN
		memo = (
            select coalesce(round(sum(atd.amount),2),0) as credit_memo from accounting.ar_transaction_details atd
			where atd.account_receivable_item_id = ar_item and atd."type" = 'memo'
			and (atd.is_voided is null or atd.is_voided = false)
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
			and (atd.is_voided is null or atd.is_voided = false)
		);
		RETURN payments;
END; $function$
;