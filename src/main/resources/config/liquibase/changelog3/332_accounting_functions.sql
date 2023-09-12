CREATE OR REPLACE FUNCTION accounting.balance_ap(ap_id uuid)
 RETURNS numeric
 LANGUAGE plpgsql
AS $function$
DECLARE balance numeric;
BEGIN
		balance = (
            select round(p.net_amount - p.applied_amount,2) as balance from accounting.payables p
            where p.id = ap_id
        );
		RETURN balance;
END; $function$
;


CREATE OR REPLACE FUNCTION accounting.ap_ledger(sup uuid)
 RETURNS TABLE(id uuid, supplier uuid, supplier_fullname varchar, ledger_type character varying, ledger_date timestamp,
 ref_no character varying, ref_id uuid, debit numeric, credit numeric, running_balance numeric, out_balance numeric, is_include bool, beg_balance numeric )
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  with ledger as (select
  al.id,
  al.supplier,
  s.supplier_fullname,
  al.ledger_type,
  al.ledger_date,
  al.ref_no,
  al.ref_id,
  round(al.debit,2) as debit,
  round(al.credit,2) as credit,
  round(sum(al.credit - al.debit) over
  (order by al.ledger_date),2) as running_balance,
  case
  	WHEN (al.ledger_type = 'AP' or al.ledger_type = 'PF')
		THEN accounting.balance_ap(al.ref_id)
		ELSE  0.00
  END as out_balance,
  al.is_include
  from accounting.ap_ledger al, inventory.supplier s where
  al.supplier = sup
  and s.id = al.supplier
  and al.is_include = true)
  select l.*, coalesce(lag(l.out_balance, 1) over (order by l.ledger_date),0) as beg_balance from ledger l;
END;
$function$
;


