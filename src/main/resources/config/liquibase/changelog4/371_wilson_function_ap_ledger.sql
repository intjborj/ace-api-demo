CREATE OR REPLACE FUNCTION accounting.balance_dis(dis_id uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE balance numeric;
BEGIN
		balance = (
            select coalesce(sum(d.voucher_amount  - d.applied_amount),0) as balance from accounting.disbursement d
			where d.is_advance = true and d.id = dis_id
        );
		RETURN balance;
END; $function$
;