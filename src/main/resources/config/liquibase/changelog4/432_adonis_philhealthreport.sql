CREATE TYPE accounting.arphilhealthicdrvs AS (
	dischargeddate date,
	admitteddate date,
	entrydate date,
	soano varchar,
	folio varchar,
	patient varchar,
	registrytype varchar,
	icdcode text,
	rvscode text,
	hci numeric,
	pf numeric ,
	total numeric
);
CREATE OR REPLACE FUNCTION accounting.arphilhealthreport(start_date date, end_date date,accountid uuid)
 RETURNS SETOF accounting.arphilhealthicdrvs
 LANGUAGE plpgsql
AS $function$
DECLARE
    item accounting.arphilhealthicdrvs%rowtype;
    x record ;
   	part varchar[];
   	parts varchar[];

begin

	for x in
		select
			to_char(date(c.admission_datetime AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD') as admissionDate,
			to_char(date(c.discharged_datetime AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD') as dischargeDate,
			to_char(date(c.discharged_datetime AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD') as entry_datetime,
			b.billing_no,
			concat(p.last_name,', ',p.first_name) as fullname,
			b.final_soa,
			c.registry_type ,
			c.icd_diagnosis,
			c.rvs_diagnosis,
			coalesce((select sum(bsi2.amount) from accounting.billing_schedule_items bsi2 where bsi2.billing_id = bsi.billing_id and bsi2.is_voided is not true and bsi2."type" = 'HCI' group by bsi2.billing_id),0) as hci ,
			coalesce((select sum(bsi2.amount) from accounting.billing_schedule_items bsi2 where bsi2.billing_id = bsi.billing_id and bsi2.is_voided is not true and bsi2."type" = 'PF' group by bsi2.billing_id),0) as pf
		from
			accounting.billing_schedule_items bsi
			left join accounting.billing_schedule bs on bs.id = bsi.billing_schedule_id
			left join billing.billing b  on b.id  = bsi.billing_id
			left join pms.patients p on p.id  = b.patient
			left join pms.cases c on c.id = b.patient_case
			where
			bsi.is_voided  is not true and
			bs.status = 'posted' and
			bs.account_id = accountid and
			to_char(date(c.discharged_datetime AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD') between  to_char(start_date,'YYYY-MM-DD')  and  to_char(end_date,'YYYY-MM-DD')
			group by
			bsi.billing_id,
			bs.status,
			b.billing_no ,
			b.patient,
			b.patient_case,
			c.icd_diagnosis ,
			c.rvs_diagnosis,
			p.last_name ,
			p.first_name,
			c.discharged_datetime,
			c.admission_datetime,
			c.registry_type,
			b.entry_datetime,
			b.final_soa
			order by c.discharged_datetime desc
	loop
		item.dischargeddate = x.dischargeDate;
		item.admitteddate = x.admissionDate;
		item.entrydate = x.entry_datetime;
		item.soano = x.final_soa;
		item.folio = x.billing_no;
		item.patient = x.fullname;
		item.registrytype = x.registry_type;
		item.hci = x.hci;
		item.pf = x.pf;
		item.icdcode = x.icd_diagnosis;
		item.rvscode = x.rvs_diagnosis;
		RETURN NEXT item;
	end loop;

end
$function$
;

