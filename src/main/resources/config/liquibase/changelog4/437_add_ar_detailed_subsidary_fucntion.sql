CREATE OR REPLACE FUNCTION accounting.ardetailedsubsidiaryledger(start_date date, end_date date, accountid uuid)
 RETURNS SETOF accounting.detailedsubsidaryar
 LANGUAGE plpgsql
AS $function$
DECLARE
    item accounting.detailedsubsidaryar%rowtype;
    x record ;
   	part varchar[];
   	parts varchar[];

begin
	for x in
		select
		dischargedate,
		patient ,
		final_soa ,
		billing_no ,
		sum(hci) as "hci" ,
		sum(pf) as "pf" ,
		sum(hci) + sum(pf) as "balance"
		from
			(select
			(
				case when to_char(date(c.discharged_datetime AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD') is null
				then
				to_char(date(c.created_date AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD')
				else
				to_char(date(c.discharged_datetime AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD')
				end
			) as dischargeDate,
			concat(p.last_name,', ',p.first_name,' ',p.middle_name) as "patient",
			concat(to_char(date(b.entry_datetime AT TIME ZONE 'Asia/Manila'),'YYYY'),'-',(case when b.final_soa is null then b.billing_no else b.final_soa end)) as final_soa,
			b.billing_no,
			(case when bi.item_type = 'DEDUCTIONS' then bi.credit else 0 end) as hci,
			(case when bi.item_type = 'DEDUCTIONSPF' then bi.credit else 0 end) as pf
			from billing.billing b
			left join billing.billing_item bi on bi.billing = b.id
			left join billing.billing_item_details bid on bid.billingitem = bi.id and bid.field_name = 'COMPANY_ACCOUNT_ID'
			left join pms.patients p  on p.id = b.patient
			left join pms.cases c  on c.id = b.patient_case
			where
			bi.item_type in ('DEDUCTIONS', 'DEDUCTIONSPF')
			and
			bid.field_value::uuid  = accountid
			and
			bi.status  = 'ACTIVE') as detailed_subsidiary
		where dischargedate between  to_char(start_date,'YYYY-MM-DD')  and  to_char(end_date,'YYYY-MM-DD')
		group by patient,dischargeDate,final_soa,billing_no
		order by dischargeDate desc
	loop
		item.dischargeddate = x.dischargedate;
		item.soano = x.final_soa;
		item.folio = x.billing_no;
		item.patient = x.patient;
		item.hci = x.hci;
		item.pf = x.pf;
		item.total = x.balance;
		RETURN NEXT item;
	end loop;

end
$function$
;
