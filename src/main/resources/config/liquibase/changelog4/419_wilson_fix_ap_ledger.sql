CREATE OR REPLACE FUNCTION accounting.balance_ap(ap_id uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE balance numeric;
BEGIN
		balance = (
            select round(p.net_amount - p.applied_amount - p.da_amount - p.dm_amount,2) as balance from accounting.payables p
            where p.id = ap_id
        );
		RETURN balance;
END; $function$
;