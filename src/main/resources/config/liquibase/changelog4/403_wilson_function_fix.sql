CREATE OR REPLACE FUNCTION inventory.movement_report(itemid uuid, depid uuid, years text)
 RETURNS SETOF inventory.movement_item
 LANGUAGE plpgsql
AS $function$
DECLARE
    item inventory.movement_item%rowtype;
    bi record ;
    jan integer;
	feb integer;
	mar integer;
	apr integer;
	may integer;
	jun integer;
	jul integer;
	aug integer;
	sep integer;
	oct integer;
	nov integer;
	decm integer;
begin

    --RAISE NOTICE ' from %', _from_date;
    -- RAISE NOTICE ' to %', _to_date;

    -- please load deduction for date deduction belong to the from - to date
    -- and date transaction is less than from
    -- gross is not registered
    jan := 0;feb := 0;mar := 0;apr := 0;may := 0;jun := 0;jul := 0;aug := 0;sep := 0;oct := 0;nov := 0;decm := 0;

    FOR bi IN
        select
        	a.item,
        	a.ledger_date,
        	a.ledger_qty_in,
        	a.ledger_qty_out
			from
				inventory.inventory_ledger a
			where
				a.source_dep = depid and
				a.item = itemid
		and a.is_include = true
        loop
        	if((date(bi.ledger_date) between to_date(concat(years,'0101')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0101'))) )) then
				jan := jan + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0201')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0201'))) )) then
				feb := feb + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0301')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0301'))) )) then
				mar := mar + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0401')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0401'))) )) then
				apr := apr + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0501')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0501'))) )) then
				may := may + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0601')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0601'))) )) then
				jun := jun + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0701')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0701'))) )) then
				jul := jul + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0801')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0801'))) )) then
				aug := aug + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'0901')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'0901'))) )) then
				sep := sep + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'1001')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'1001'))) )) then
				oct := oct + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'1101')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'1101'))) )) then
				nov := nov + coalesce(bi.ledger_qty_out,0);
        	end if;
        	if((date(bi.ledger_date) between to_date(concat(years,'1201')::text, 'YYYYMMDD') and (select inventory.last_day(concat(years,'1201'))) )) then
				decm := decm + coalesce(bi.ledger_qty_out,0);
        	end if;
        	item.id := bi.item;
        END LOOP;
       	item.jan = jan;
		item.feb = feb;
		item.mar = mar;
		item.apr = apr;
		item.may = may;
		item.jun = jun;
		item.jul = jul;
		item.aug = aug;
		item.sep = sep;
		item.oct = oct;
		item.nov = nov;
		item.decm = decm;
		item.total = jan+feb+mar+apr+may+jun+jul+aug+sep+oct+nov+decm;
	    return next item;
end
$function$
;