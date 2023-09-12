CREATE INDEX IF NOT EXISTS billing_item_transaction_date_idx ON billing.billing_item (transaction_date);
CREATE INDEX IF NOT EXISTS payment_tracker_billingid_idx ON cashiering.payment_tracker (billingid);
CREATE INDEX IF NOT EXISTS billing_item_item_type_idx ON billing.billing_item (item_type);
CREATE INDEX IF NOT EXISTS billing_item_status_idx ON billing.billing_item (status);


CREATE OR REPLACE VIEW billing.billingitem_service_view
AS select billingitem , field_value,svc.process_code ,svc.description as service from billing.billing_item_details bid
   left join  ancillary.services  svc on svc.id = bid.field_value :: uuid
   where field_name ='SERVICEID';

CREATE OR REPLACE VIEW billing.billingitem_item_view
AS select billingitem , field_value, itm.desc_long , itm.item_code as itemcode from billing.billing_item_details bid
   left join inventory.item  itm on itm.id=bid.field_value ::uuid
   where field_name ='ITEMID';

CREATE OR REPLACE VIEW billing.billingitem_item_discounts as
 select * from billing.billing_item_details bd where bd.field_name = 'DISCOUNT_ID';

CREATE OR REPLACE VIEW billing.billingitem_item_companyaccounts as
select * from billing.billing_item_details bd where bd.field_name = 'COMPANY_ACCOUNT_ID';


CREATE OR REPLACE VIEW billing.billingitem_deductionshci as
select bi.*, ba.amount as amountdeduct, bi2.description as deduction_desc, bi2.record_no deductionreno,
       dsc.field_value  as  discountid, cpy.field_value  as companyaccountid,
       bi2.transaction_date  as date_deducted
from billing.billingitems_amountdetails ba
         left join billing.billing_item bi on bi.id = ba.billingitemsid::uuid
         left join billing.billing_item bi2 on bi2.id = ba.billingitem ::uuid
         left join billing.billingitem_item_discounts dsc on dsc.billingitem  = ba.billingitem
         left join billing.billingitem_item_companyaccounts cpy on cpy.billingitem  = ba.billingitem
where bi2.item_type = 'DEDUCTIONS' and bi2.status = 'ACTIVE';

CREATE OR REPLACE VIEW billing.billingitem_deductionshcisimple as
select bi.*, ba.amount as amountdeduct, bi2.description as deduction_desc, bi2.record_no deductionreno,
       bi2.transaction_date  as date_deducted
from billing.billingitems_amountdetails ba
         left join billing.billing_item bi on bi.id = ba.billingitemsid::uuid
         left join billing.billing_item bi2 on bi2.id = ba.billingitem ::uuid
where bi2.item_type = 'DEDUCTIONS' and bi2.status = 'ACTIVE';



CREATE TYPE   billing.salesreportitem AS
(
    id uuid,
    "category" character varying ,
    date timestamp without time zone,
    ornos character varying ,
    folio character varying ,
    recno character varying ,
    department character varying ,
    process_code character varying ,
    service character varying ,
    gross numeric(15, 2),
    discounts_availed character varying ,
    investors numeric(15, 2),
    discounts_total numeric(15, 2),
    net_sales numeric(15, 2)
);



CREATE OR REPLACE FUNCTION billing.getornumbers(_billingid uuid)
    RETURNS character varying
    LANGUAGE plpgsql
AS $function$DECLARE
    ornumbers varchar[][];
    temprecord record;

BEGIN

    if  _billingid is null then
        return '';
    end if;

    for temprecord in
        select distinct pt.ornumber  from cashiering.payment_tracker pt  where pt.billingid = _billingid
        loop

            ornumbers := array_append(ornumbers,temprecord.ornumber);

        end loop;

    if FOUND <> true then
        return '';
    end if;

    return array_to_string(ornumbers,',','');
END;
$function$
;



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
        SELECT    bli.id,bli.transaction_date,bli.record_no, bli.qty , bli.debit , bli.credit ,
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


            item.service = coalesce(bi.service,bi.desc_long,'');
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
        SELECT    bli.id,bli.transaction_date,bli.record_no, bli.qty , bli.debit , bli.credit ,
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


            item.service = coalesce(bi.service,bi.desc_long,'');
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
