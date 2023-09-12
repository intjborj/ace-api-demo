DROP FUNCTION billing.revenue_recon_report(date, date);
DROP TYPE billing.revenue_recon_report;

CREATE TYPE billing.revenue_recon_report AS (
	id uuid,
	folio_no varchar,
	case_no varchar,
	soa_no varchar,
	patient_name varchar,
	registration_type varchar,
	admission_date varchar,
	discharge_date varchar,
	transaction_date varchar,
	maker_name varchar,
	maker_section varchar,
	maker_department varchar,
	revenue_center varchar,
	record_no varchar,
	item_code varchar,
	description varchar,
	quantity int4,
	price numeric(15,2),
	room_board numeric(15,2),
	drugs_medicine numeric(15,2),
	laboratory_diagnostic numeric(15,2),
	operating_room_fee numeric(15,2),
	supplies numeric(15,2),
	oxygen numeric(15,2),
	infusion_pump numeric(15,2),
	miscellaneous_services numeric(15,2),
	gross_sales_vat_exclusive numeric(15,2),
	vat_revenue numeric(15,2),
	vat_exempt numeric(15,2),
	vat numeric(15,2),
	liability_pf numeric(15,2),
	deduction_hci numeric(15,2),
	deduction_pf numeric(15,2),
	shift_no varchar,
	terminal varchar,
	payment_reference_no varchar,
	mode_of_payment varchar,
	payment_amount numeric(15,2),
	remarks varchar,
	outstanding_balance numeric(15,2));

CREATE OR REPLACE FUNCTION billing.revenue_recon_report(_from_date date, _to_date date)
 RETURNS SETOF billing.revenue_recon_report
 LANGUAGE plpgsql
AS $function$
DECLARE
    item billing.revenue_recon_report%rowtype;
   	voided billing.revenue_recon_report%rowtype;
    bill record ;
   	vat_exclusive numeric;
   	qty numeric;
begin

	for bill in select
		bi.id,
		b.billing_no as "billing_no",
		c.case_no as "case_no",
		concat(extract('Year' from b.created_date)::int,'-',bi.registry_type_charged,'-',b.billing_no) as "soa_no",
		concat(p.last_name,', ',p.first_name,' ',p.middle_name) as "patient_name",
		bi.registry_type_charged as "registration_type",
		to_char(date(c.admission_datetime + interval '8 hours'),'YYYY-MM-DD') as "admission_date",
		to_char(date(c.discharged_datetime  + interval '8 hours'),'YYYY-MM-DD') as "discharge_date",
		to_char(date(bi.transaction_date  + interval '8 hours'),'YYYY-MM-DD') as "transaction_date",
		to_char(date(bi.last_modified_date   + interval '8 hours'),'YYYY-MM-DD') as "voided_date",
		bi.created_by as "maker_name",
		d.department_name as "maker_section",
		bi.record_no as "record_no",
		bi.description  as "description",
		bi.qty as "qty",
		abs(coalesce(bi.debit,0)-coalesce(bi.credit,0))  as "price",
		case
			when d.parent_department is not null then
			(select d2.department_name  from public.departments d2  where d2.id  = d.parent_department)
			else
			d.department_name
		end as "maker_department",
		case
			when bi.item_type = 'MEDICINES'
			then 'PHARMACY'
			when bi.item_type = 'SUPPLIES'
			then 'CENTRAL STERILE SUPPLY ROOM'
			when d.parent_department is not null
			then null
			when d.revenue_center is true
			then d.department_name
			else null
		end as "revenue_center",
		case
			when bi.status  != 'ACTIVE'
			then false else true
		end as "status",
		bi.item_type,
		case
		when bi.item_type  in ('MEDICINES','SUPPLIES')
		then
			(
			select
			json_build_object('item_code',i.item_code,'vatable',coalesce(i.vatable,false),'is_gas',coalesce(i.gas,false))
			from billing.billing_item_details bid
			left join inventory.item i on bid.field_value::uuid = i.id
			where bid.field_name = 'ITEMID' and bid.billingitem = bi.id
			)
		else null
		end as "inventory",
		case
		when bi.item_type  in ('DEDUCTIONS','DEDUCTIONSPF')
		then
			(
				select d.include_vat  from billing.billing_item_details bid
				left join billing.discounts d on d.id  = bid.field_value::uuid
				where bid.field_name  = 'DISCOUNT_ID' and bid.billingitem  = bi.id
			)
		else false
		end as "isDiscVatable",
		case
		when bi.item_type = 'PAYMENTS'
		then
		(select
			json_build_object ('receipt_type',pt.receipt_type,
			'total_cash',coalesce(pt.total_cash,0) ,
			'total_check',coalesce(pt.total_check,0) ,
			'total_card',coalesce(pt.total_card,0) ,
			'total_deposit',coalesce(pt.total_deposit,0) ,
			'or_number',pt.ornumber,
			'shift_no',s.shiftno,
			'terminal',c2.terminal_id,
			'totalpayments',coalesce(pt.totalpayments,0)
			) as "field" from billing.billing_item_details bid
			left join cashiering.payment_tracker pt on pt.id  = bid.field_value::uuid
			left join cashiering.shifting s on s.id = pt.shiftid
			left join cashiering.cashierterminals c2 on c2.id = s.cashier
			where bid.billingitem = bi.id and bid.field_name  = 'PAYTRACKER_ID'
		)
		else null
		end as "cashiering",
		case
			when bi.canceledref is not null
			then true else false
		end as "returned_item",
		case
			when bi.item_type = 'DEDUCTIONS'
			then (
					select coalesce (c.companyname,'')
					from billing.billing_item_details bid
					left join billing.companyaccounts c on c.id  = bid.field_value::uuid
					where bid.billingitem  = bi.id and bid.field_name = 'COMPANY_ACCOUNT_ID')
			else null
		end as "company_account"
		from billing.billing_item bi
		left join billing.billing b on b.id = bi.billing
		left join pms.cases c on c.id  = b.patient_case
		left join pms.patients p on p.id = b.patient
		left join public.departments d on d.id  = bi.department
		left join public.t_user tu on tu.login = bi.created_by
		where
		bi.item_type != 'ANNOTATION_PAYMENTS_GROUPS' and
		(
			(to_char(date(bi.transaction_date  + interval '8 hours'),'YYYY-MM-DD') between  to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD'))
			or
			(
				(to_char(date(bi.created_date  + interval '8 hours'),'YYYY-MM-DD') != to_char(date(bi.last_modified_date  + interval '8 hours'),'YYYY-MM-DD') and bi.status != 'ACTIVE') and
				 (to_char(date(bi.last_modified_date  + interval '8 hours'),'YYYY-MM-DD') between  to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD') and bi.status != 'ACTIVE')
			)
		)
	loop
		item.id = bill.id;
		item.folio_no =  bill.billing_no;
		item.case_no =  bill.case_no;
		item.soa_no =  bill.soa_no;
		item.patient_name =  bill.patient_name;
		item.registration_type =  bill.registration_type;
		item.admission_date =  bill.admission_date;
		item.discharge_date =  bill.discharge_date;
		item.transaction_date =  bill.transaction_date;
		item.maker_name =  bill.maker_name;
		item.maker_section =  bill.maker_section;
		item.maker_department =  bill.maker_department;
		item.record_no =  bill.record_no;
		item.item_code =  '';
		item.description =  bill.description;

		item.price =  bill.price;

		if(bill.revenue_center is null) then
			item.revenue_center = bill.maker_department;
		else
			item.revenue_center =  bill.revenue_center;
		end if;

		if(bill.returned_item is true) then
			item.description = substring(item.description,19);
			item.remarks = 'RETURNED ';
			item.quantity =  bill.qty * -1;
		else
			item.quantity =  bill.qty;
			item.remarks = '';
		end if;

		if(bill.status is false) then
			voided = item;
			if(bill.returned_item is false) then
				qty = 1;
			end if;
			voided.quantity =  bill.qty * -1;
			voided.transaction_date = bill.voided_date;
			voided.price =  bill.price;
			voided.record_no = bill.record_no;
		else voided = null;
		end if;



		item.vat_revenue = 0;
		item.vat_exempt = 0;
		item.vat = 0;
		vat_exclusive = 0;

		if (bill.item_type not in ('PF','DEDUCTIONS','DEDUCTIONSPF','PAYMENTS')) then
			if((coalesce(bill.inventory->>'vatable','false')::boolean is true or bill."isDiscVatable" is true) and bill.registration_type = 'OTC') then
			item.vat_revenue = (bill.price * item.quantity) / 1.12;
			item.vat = item.vat_revenue * 0.12;
			item.vat_exempt = 0;
			else
			item.vat_revenue = 0;
			item.vat_exempt = (bill.price * item.quantity);
			item.vat = 0;
			end if;
		end if;

		vat_exclusive = item.vat_revenue + item.vat_exempt;

		if bill.item_type = 'ROOMBOARD' then
		item.room_board = vat_exclusive;
		else item.room_board = 0;
		end if;

		if bill.item_type = 'MEDICINES' then
		item.item_code = bill.inventory->>'item_code';
		item.drugs_medicine = vat_exclusive;
		else
			item.drugs_medicine = 0;
			item.item_code = '';
		end if;


		if bill.item_type = 'ORFEE' then
		item.operating_room_fee = vat_exclusive;
		else item.operating_room_fee = 0;
		end if;

		if bill.item_type = 'SUPPLIES' then
			if bill.inventory->>'is_gas' then
				item.item_code = bill.inventory->>'item_code';
				item.oxygen = vat_exclusive;
				item.supplies = 0;
			else
				item.item_code = bill.inventory->>'item_code';
				item.supplies = vat_exclusive;
				item.oxygen = 0;
			end if;
		else
			item.item_code = '';
			item.supplies = 0;
			item.oxygen = 0;
		end if;

		if bill.item_type = 'OTHERS' then
			if bill.description like '%USE OF INFUSION PUMP%' then
				item.infusion_pump = vat_exclusive;
				item.miscellaneous_services = 0;
			else
				item.miscellaneous_services = vat_exclusive;
				item.infusion_pump = 0;
			end if;
		else
			item.miscellaneous_services = 0;
			item.infusion_pump = 0;
		end if;

		if bill.item_type = 'DIAGNOSTICS' then
			if bill.maker_section = 'BAYVIEW DINING' then
				item.miscellaneous_services = vat_exclusive;
				item.laboratory_diagnostic = 0;
			else
				item.laboratory_diagnostic = vat_exclusive;
			end if;
		else item.laboratory_diagnostic = 0;
		end if;



		if( bill.item_type = 'PF') then
		item.liability_pf = bill.price * item.quantity;
		else item.liability_pf = 0;
		end if;

		if( bill.item_type = 'DEDUCTIONS') then
			if(bill.company_account is not null) then
				item.remarks = concat(item.remarks,' ',bill.company_account,' ');
			end if;
		item.deduction_hci = bill.price * item.quantity;
		else
			item.deduction_hci = 0;
			item.remarks = item.remarks;
		end if;

		if( bill.item_type = 'DEDUCTIONSPF') then
		item.deduction_pf = bill.price * item.quantity;
		else item.deduction_pf = 0;
		end if;

		if( bill.item_type = 'PAYMENTS') then
		item.shift_no  = bill.cashiering->>'shift_no';
		item.terminal = bill.cashiering->>'terminal';
		item.payment_reference_no = concat(bill.cashiering->>'receipt_type',':',bill.cashiering->>'or_number');
		item.payment_amount = coalesce(bill.cashiering->>'totalpayments','0')::numeric;
			if(coalesce(bill.cashiering->>'total_cash','0')::numeric > 0) then
				item.mode_of_payment = concat('CASH :',coalesce(bill.cashiering->>'total_cash','0'),' ,')::varchar;
			else
				item.mode_of_payment = '';
			end if;

			if (coalesce(bill.cashiering->>'total_check','0')::numeric > 0) then
				item.mode_of_payment = concat(item.mode_of_payment, 'CHECK :',coalesce(bill.cashiering->>'total_check','0'),' ,')::varchar;
			end if;

			if (coalesce(bill.cashiering->>'total_card','0')::numeric > 0) then
				item.mode_of_payment =  concat(item.mode_of_payment, 'CARD :',coalesce(bill.cashiering->>'total_card','0'),' ,')::varchar;
			end if;

			if (coalesce(bill.cashiering->>'total_deposit','0')::numeric > 0) then
				item.mode_of_payment =  concat(item.mode_of_payment, 'DEPOSIT :',coalesce(bill.cashiering->>'total_deposit','0'),' ,')::varchar;
			end if;


		else
			item.payment_amount = 0;
			item.payment_reference_no = '';
			item.mode_of_payment = '';
			item.shift_no = '';
			item.terminal = '';
		end if;

--		NOTE: gross_sales_vat_exclusive change to vat incluse
--		wla nalng nahu usba kay dghan kaau usbon
		item.gross_sales_vat_exclusive = vat_exclusive + item.vat;
		item.outstanding_balance = vat_exclusive + item.vat + item.liability_pf - item.deduction_hci - item.deduction_pf - item.payment_amount;

		if(bill.status is false and bill.voided_date between to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD')) then
		voided.room_board = item.room_board * -1;
		voided.drugs_medicine = item.drugs_medicine * -1;
		voided.laboratory_diagnostic = item.laboratory_diagnostic * -1;
		voided.operating_room_fee = item.operating_room_fee * -1;
		voided.supplies = item.supplies * -1;
		voided.miscellaneous_services = item.miscellaneous_services * -1;
		voided.liability_pf = item.liability_pf * -1;
		voided.deduction_hci = item.deduction_hci * -1;
		voided.deduction_pf = item.deduction_pf * -1;

			if( bill.item_type = 'PAYMENTS') then
				voided.payment_amount = item.payment_amount * -1;
			else
				voided.payment_amount = 0;
				voided.payment_reference_no = '';
				voided.mode_of_payment = '';
				voided.shift_no = '';
				voided.terminal = '';
			end if;
		voided.vat_revenue = item.vat_revenue * -1;
		voided.vat = item.vat * -1;
		voided.vat_exempt = item.vat_exempt * -1;
		voided.gross_sales_vat_exclusive = item.gross_sales_vat_exclusive * -1;
		voided.outstanding_balance = item.outstanding_balance * -1 ;
		if(item.remarks = '') then
			voided.remarks = 'VOIDED';
		else
			voided.remarks = concat(item.remarks, '- VOIDED');
		end if;
		return next voided;
		end if;
		if(bill.transaction_date between to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD')) then
		return next item;
		end if;


	end loop;

end
$function$
;


