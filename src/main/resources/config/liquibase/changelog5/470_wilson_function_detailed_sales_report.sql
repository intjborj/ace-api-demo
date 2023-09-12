CREATE OR REPLACE FUNCTION billing.detailed_sales_report(filter_type varchar, start_date date, end_date date)
 RETURNS TABLE(
 	id UUID,
 	transaction_date timestamp without time zone,
	folio_no varchar,
	billing_reference_no varchar,
	barcode varchar,
	item_code varchar,
	service_code varchar,
	description varchar,
	department varchar,
	qty numeric,
	amount numeric,
	total_amount numeric
	)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  select 
  	bi.id,
  	bi.transaction_date,
	b.billing_no as folio_no,
	bi.record_no as billing_reference_no,
	i.sku as barcode,
	i.item_code,
	s.service_code,
	bi.description,
	d.department_name as department,
	bi.qty,
	(coalesce(bi.debit,0) - coalesce(bi.credit,0)) as amount,
	(bi.qty * (coalesce(bi.debit,0) - coalesce(bi.credit,0))) as total_amount
	from billing.billing_item bi 
		left join billing.billing b on b.id = bi.billing 
		left join public.departments d on d.id = bi.department
		left join billing.billing_item_details bid on bid.billingitem = bi.id and bid.field_name in ('ITEMID', 'SERVICEID')
		left join inventory.item i on i.id = bid.field_value::uuid
		left join ancillary.services s on s.id = bid.field_value::uuid
	where bi.item_type = filter_type and bi.status  = 'ACTIVE' and date(bi.transaction_date) between start_date and end_date
	order by bi.transaction_date;
END;
$function$
;