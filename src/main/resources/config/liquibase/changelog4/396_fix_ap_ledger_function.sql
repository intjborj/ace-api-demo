CREATE OR REPLACE FUNCTION accounting.ap_ledger(sup uuid)
 RETURNS TABLE(id uuid, supplier uuid, supplier_fullname character varying, ledger_type character varying, ledger_date timestamp without time zone, ref_no character varying, ref_id uuid, debit numeric, credit numeric, running_balance numeric, out_balance numeric, is_include boolean, beg_balance numeric)
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
  	WHEN (al.ledger_type = 'AP' or al.ledger_type = 'PF' or al.ledger_type = 'RF')
		THEN accounting.balance_ap(al.ref_id)
		ELSE  accounting.balance_dis(al.ref_id)
  END as out_balance,
  al.is_include
  from accounting.ap_ledger al, inventory.supplier s where
  al.supplier = sup
  and s.id = al.supplier
  and al.is_include = true and (al.deleted is null or al.deleted = false))
  select l.*, coalesce(lag(l.running_balance, 1) over (order by l.ledger_date),0) as beg_balance from ledger l;
END;
$function$
;
