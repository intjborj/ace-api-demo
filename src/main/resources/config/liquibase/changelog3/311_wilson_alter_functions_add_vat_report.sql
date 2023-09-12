
ALTER TYPE billing.salesreportitem DROP ATTRIBUTE IF EXISTS vatable_sales;
ALTER TYPE billing.salesreportitem ADD ATTRIBUTE vatable_sales numeric;

ALTER TYPE billing.salesreportitem DROP ATTRIBUTE IF EXISTS vat_exempt_sales;
ALTER TYPE billing.salesreportitem ADD ATTRIBUTE vat_exempt_sales numeric;

ALTER TYPE billing.salesreportitem DROP ATTRIBUTE IF EXISTS vat_amount;
ALTER TYPE billing.salesreportitem ADD ATTRIBUTE vat_amount numeric;

----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION billing.vatable_sales(net numeric, vat_rate numeric)
RETURNS numeric
LANGUAGE plpgsql
AS $function$
DECLARE value numeric;
BEGIN
	value = (select round(net / ((vat_rate / 100.00) + 1.00),2) as value);
	RETURN value;
END; $function$
;

-------------------------------
CREATE OR REPLACE VIEW billing.billingitem_item_view
AS SELECT bid.billingitem,
    bid.field_value,
    itm.desc_long,
    itm.item_code AS itemcode,
    itm.vatable
   FROM billing.billing_item_details bid
     LEFT JOIN inventory.item itm ON itm.id = bid.field_value::uuid
  WHERE bid.field_name::text = 'ITEMID'::text;

--------------------------------------
CREATE OR REPLACE FUNCTION billing.vat_amount(before_vat numeric, vat_rate numeric)
RETURNS numeric
LANGUAGE plpgsql
AS $function$
DECLARE value numeric;
BEGIN
	value = (select round(before_vat * (vat_rate/100.00),2) as value);
	RETURN value;
END; $function$
;

------------------------------------
CREATE OR REPLACE FUNCTION billing.salesreport_1(_from_date date, _to_date date, _department character varying)
 RETURNS SETOF billing.salesreportitem
 LANGUAGE plpgsql
AS $function$
DECLARE
    item billing.salesreportitem%rowtype;
    bi record ;
    parts varchar[][];
    part varchar[];
    tmp varchar ;
    clean varchar ;
    amtdeduct numeric;
    totaldiscount numeric;

begin

    --RAISE NOTICE ' from %', _from_date;
    -- RAISE NOTICE ' to %', _to_date;

    -- please load deduction for date deduction belong to the from - to date
    -- and date transaction is less than from
    -- gross is not registered

    FOR bi IN
        SELECT    bli.id,bli.transaction_date,bli.record_no, bli.qty , bli.debit , bli.credit ,bli.description ,
                  bl.billing_no,bl.id as billingid,
                  cs.registry_type , bl.otcname,
                  dp.department_name,dp.id as deptid, dp.parent_department  as deptpid,
                  svc.service ,svc.process_code , svc.service_code ,
                  itm.desc_long ,itm.itemcode ,
                  deduct.dedval,
                  bli.date_deducted,
                  bli.discountid
        FROM billing.billingitem_deductionshci  bli
                 left  join billing.billing bl on bl.id=bli.billing
                 left  join pms.cases cs on cs.id = bl.patient_case
                 left  join billing.billingitem_simpleservice_view_with_department svc on svc.billingitem =bli.id
                 left  join billing.billingitem_simpleinventory_view_with_department invry on invry.billingitem =bli.id
                 left  join public.departments dp on dp.id = coalesce(svc.svc_department ,invry.inventory_department, bli.department)
                 left  join billing.billingitem_item_view itm on itm.billingitem =bli.id
                 left  join (select id, string_agg(concat(discountid ,';',deduction_desc,';',amountdeduct),',') as dedval from billing.billingitem_deductionshci
                             where discountid is not null group by id,date_deducted) deduct  on deduct.id  = bli.id

        where bli.item_type in ('MEDICINES','ROOMBOARD','SUPPLIES','OTHERS','DIAGNOSTICS','ORFEE')
          and to_char(date(bli.transaction_date AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD')
            not between  to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD')
          and to_char(date(bli.date_deducted AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD')
            between  to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD')
          and coalesce(dp.parent_department::text,dp.id::text) like concat('%',_department,'%')
          and bli.status='ACTIVE'

        order by bli.record_no
        loop
        if(bi.discountid is not null) then
            item.id = bi.id;
            item.date = bi.transaction_date;
            item.department = bi.department_name;
            item.recno = bi.record_no;
            item.folio = bi.billing_no;
            item.gross = 0;
            item.ornos = billing.getornumbers(bi.billingid);
            item.discounts_availed := '';
            item.discounts_total := 0;
            if(bi.dedval is not null) then
                parts  :=  regexp_split_to_array(bi.dedval::text,','::text);
                totaldiscount := 0;
                FOREACH part SLICE 1 IN ARRAY parts
                    loop
                        clean := replace(part::text,'{','');
                        clean := replace(clean,'}','');
                        clean := replace(clean,'"','');
                        tmp := split_part(clean::text,';',2);
                        item.discounts_availed = concat(item.discounts_availed,tmp,',');
                        tmp := split_part(clean::text,';',3);
                        amtdeduct := tmp::numeric;
                        totaldiscount := totaldiscount + amtdeduct;

                    END LOOP;
                item.discounts_total := totaldiscount;
            end if;

            item.net_sales =  item.gross -  item.discounts_total;


            item.service = coalesce(bi.service,bi.desc_long,bi.description,'');
           	item.service_code = coalesce(bi.service_code,'');
            item.process_code = coalesce(bi.process_code,bi.itemcode,'');

            IF bi.otcname is not null  THEN
                item.category := 'OTC';
               item.vatable_sales = 0;
                item.vat_exempt_sales = 0;
               	item.vat_amount = 0;
            elsif bi.registry_type = 'IPD' then
                item.category := 'IPD';
               item.vatable_sales = 0;
                item.vat_exempt_sales = 0;
               	item.vat_amount = 0;
            else
                item.category := 'OPD';
               	item.vatable_sales = 0;
                item.vat_exempt_sales = 0;
               	item.vat_amount = 0;
            end if;

            RETURN NEXT item;
           end if;
        END LOOP;

        -------   This is for the current Items
    FOR bi IN
        SELECT    bli.id,bli.transaction_date,bli.record_no, bli.qty , bli.debit , bli.credit ,bli.description,
                  bl.billing_no,bl.id as billingid,
                  cs.registry_type , bl.otcname,
                  dp.department_name,dp.id as deptid, dp.parent_department  as deptpid,
                  svc.service ,svc.process_code , svc.service_code ,
                  itm.desc_long ,itm.itemcode , itm.vatable,
                  deduct.dedval, ptd.is_vatable, ptd.vat_rate
        FROM billing.billing_item  bli
                 left  join billing.billing bl on bl.id=bli.billing
                 left  join pms.cases cs on cs.id = bl.patient_case
                 left  join billing.billingitem_simpleservice_view_with_department svc on svc.billingitem =bli.id
                 left  join billing.billingitem_inventory_view_with_department invry on invry.billingitem =bli.id
                 left  join public.departments dp on dp.id = coalesce(svc.svc_department ,invry.inventory_department, bli.department)
                 left  join billing.billingitem_item_view itm on itm.billingitem =bli.id
                 left  join billing.price_tier_details ptd on ptd.id = bli.pricing_tier
                 left  join (select id,date_deducted, string_agg(concat(discountid ,';',deduction_desc,';',amountdeduct),',') as dedval from billing.billingitem_deductionshci
                             where discountid is not null group by id,date_deducted) deduct  on deduct.id  = bli.id
            and to_char(date(deduct.date_deducted AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD')
                                                                                                    between  to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD')

        where bli.item_type in ('MEDICINES','ROOMBOARD','SUPPLIES','OTHERS','DIAGNOSTICS','ORFEE')
          and to_char(date(bli.transaction_date AT TIME ZONE 'Asia/Manila'),'YYYY-MM-DD')
            between  to_char(_from_date,'YYYY-MM-DD')  and  to_char(_to_date,'YYYY-MM-DD')
          and coalesce(dp.parent_department::text,dp.id::text) like concat('%',_department,'%')
          and bli.status='ACTIVE'

        order by bli.record_no
        loop

            item.id = bi.id;
            item.date = bi.transaction_date;
            item.department = bi.department_name;
            item.recno = bi.record_no;
            item.folio = bi.billing_no;
            item.gross = bi.qty * (coalesce(bi.debit,0) - coalesce(bi.credit,0));
            item.ornos = billing.getornumbers(bi.billingid);
            item.discounts_availed := '';
            item.discounts_total := 0;
            if(bi.dedval is not null) then
                parts  :=  regexp_split_to_array(bi.dedval::text,','::text);
                totaldiscount := 0;
                FOREACH part SLICE 1 IN ARRAY parts
                    loop

                        clean := replace(part::text,'{','');
                        clean := replace(clean,'}','');
                        clean := replace(clean,'"','');
                        tmp := split_part(clean::text,';',2);
                        item.discounts_availed = concat(item.discounts_availed,tmp,',');
                        tmp := split_part(clean::text,';',3);
                        amtdeduct := tmp::numeric;
                        totaldiscount := totaldiscount + amtdeduct;

                    END LOOP;
                item.discounts_total := totaldiscount;
            end if;

            item.net_sales =  item.gross -  item.discounts_total;


            item.service = coalesce(bi.service,bi.desc_long,bi.description,'');
            item.service_code = coalesce(bi.service_code,'');
            item.process_code = coalesce(bi.process_code,bi.itemcode,'');

            IF bi.otcname is not null  THEN
                item.category := 'OTC';
               	if(bi.vatable and bi.is_vatable) then
               	item.vatable_sales = billing.vatable_sales(item.net_sales, bi.vat_rate);
                item.vat_amount = billing.vat_amount(item.vatable_sales, bi.vat_rate);
                item.vat_exempt_sales = 0;
               	else
               	item.vatable_sales = 0;
                item.vat_amount = 0;
                item.vat_exempt_sales = item.net_sales;
               	end if;
            elsif bi.registry_type = 'IPD' then
                item.category := 'IPD';
               	item.vatable_sales = 0;
                item.vat_exempt_sales = item.net_sales;
               	item.vat_amount = 0;
            else
                item.category := 'OPD';
               	item.vatable_sales = 0;
                item.vat_exempt_sales = item.net_sales;
               	item.vat_amount = 0;
            end if;
            RETURN NEXT item;
        END LOOP;
end
$function$
;