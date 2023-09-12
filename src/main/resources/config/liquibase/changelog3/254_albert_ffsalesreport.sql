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
                  svc.service ,svc.process_code ,
                  itm.desc_long ,itm.itemcode ,
                  deduct.dedval,
                  bli.date_deducted
        FROM billing.billingitem_deductionshci  bli
                 left  join billing.billing bl on bl.id=bli.billing
                 left  join pms.cases cs on cs.id = bl.patient_case
                 left  join public.departments dp on dp.id = bli.department
                 left  join billing.billingitem_service_view svc on svc.billingitem =bli.id
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
            item.process_code = coalesce(bi.process_code,bi.itemcode,'');

            IF bi.otcname is not null  THEN
                item.category := 'OTC';
            elsif bi.registry_type = 'IPD' then
                item.category := 'IPD';
            else
                item.category := 'OPD';
            end if;





            RETURN NEXT item;
        END LOOP;

    -------   This is for the current Items
    FOR bi IN
        SELECT    bli.id,bli.transaction_date,bli.record_no, bli.qty , bli.debit , bli.credit ,bli.description,
                  bl.billing_no,bl.id as billingid,
                  cs.registry_type , bl.otcname,
                  dp.department_name,dp.id as deptid, dp.parent_department  as deptpid,
                  svc.service ,svc.process_code ,
                  itm.desc_long ,itm.itemcode ,
                  deduct.dedval
        FROM billing.billing_item  bli
                 left  join billing.billing bl on bl.id=bli.billing
                 left  join pms.cases cs on cs.id = bl.patient_case
                 left  join public.departments dp on dp.id = bli.department
                 left  join billing.billingitem_service_view svc on svc.billingitem =bli.id
                 left  join billing.billingitem_item_view itm on itm.billingitem =bli.id
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
            item.process_code = coalesce(bi.process_code,bi.itemcode,'');

            IF bi.otcname is not null  THEN
                item.category := 'OTC';
            elsif bi.registry_type = 'IPD' then
                item.category := 'IPD';
            else
                item.category := 'OPD';
            end if;





            RETURN NEXT item;
        END LOOP;
    RETURN;

end
$function$
;
