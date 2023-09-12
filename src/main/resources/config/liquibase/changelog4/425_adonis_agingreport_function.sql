CREATE OR REPLACE FUNCTION accounting.aragingreport(filter_date date, account uuid)
 RETURNS SETOF accounting.aragingreportperpatient
 LANGUAGE plpgsql
AS $function$
DECLARE
    item accounting.aragingreportperpatient%rowtype;
    x record ;

begin

	for x in
		select
			trans_date,companyname,soa_no,ar_no,names,sum(debit)-sum(credit) as amount
		from
			(
				select
						c.companyname,
						ar.ar_no,ar.transaction_date as trans_date,
						(
							select
										bs.billing_schedule_no
							from accounting.ar_group ag2
									left join accounting.billing_schedule bs on bs.id = ag2.field_value
							where ag2.field_name = 'BILLING_SCHEDULE_ID'
								and ag2.account_receivable = ag.account_receivable
						) 	as soa_no,
						(
							select
										concat(p.last_name ,', ', p.first_name )
							from accounting.account_receivable_items_details arid
									left join pms.patients p on p.id = arid.field_value::uuid
							where arid.field_name = 'PATIENT_ID'
								and arid.account_receivable_items = ari.id
						) 	as names,
						coalesce(sum(ari.amount) ,0) as debit,
						coalesce(
						(
							select sum(atd.amount)
							from accounting.ar_transaction_details atd
							where atd."type" = 'payments'
								and atd.account_receivable_item_id = ari.id
								and atd.is_voided != true
							group by atd.account_receivable_item_id
						)
						,0) as credit
				from accounting.ar_group ag
						left join billing.companyaccounts c on c.id = ag.field_value
						left join accounting.account_receivable ar  on ar.id = ag.account_receivable
						left join accounting.account_receivable_items ari on ari.account_receivable_id = ag.account_receivable
				where ag.field_value = account
					and ar.status = 'active'
					and to_char(ar.transaction_date,'YYYY-MM-DD') <= to_char(filter_date,'YYYY-MM-DD')
				group  by c.companyname,ari.id,ar.ar_no,ar.transaction_date,soa_no
				order by trans_date,ar.ar_no,names,debit
			) as companyAR
				group by companyname,ar_no,names,trans_date,soa_no
	loop
		item.billing_date = x.trans_date;
		item.soa_no = LTRIM(x.soa_no,'BSN-');
		item.arno = LTRIM(x.ar_no,'ARNO-');
		item.patient = x.names;
		item.due_date = x.trans_date;
		item.current_days = 0;
		item.day_1_to_30 = 0;
		item.day_31_to_60 = 0;
		item.day_61_to_90 = 0;
		item.day_91_to_120 = 0;
		item.day_older = 0;
        item.total = 0;

		if (((filter_date - x.trans_date) > 0) and ((filter_date - x.trans_date) < 31)) then
			item.day_1_to_30 = x.amount;
		elseif (((filter_date - x.trans_date) > 30) and ((filter_date - x.trans_date) < 61)) then
			item.day_31_to_60 = x.amount;
		elseif (((filter_date - x.trans_date) > 60) and ((filter_date - x.trans_date) < 91)) then
			item.day_61_to_90 = x.amount;
		elseif (((filter_date - x.trans_date) > 90) and ((filter_date - x.trans_date) < 121)) then
			item.day_91_to_120 = x.amount;
		elseif ((filter_date - x.trans_date) > 120) then
			item.day_older = x.amount;
		else
			item.current_days = x.amount;
		end if;

		item.total = item.current_days + item.day_1_to_30 + item.day_31_to_60 + item.day_61_to_90 + item.day_91_to_120 + item.day_older;

		RETURN NEXT item;
	end loop;

end
$function$
;