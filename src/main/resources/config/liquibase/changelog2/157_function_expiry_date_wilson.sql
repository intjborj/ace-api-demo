CREATE OR REPLACE FUNCTION inventory.expiry_date(itemid uuid)
 RETURNS date
 LANGUAGE plpgsql
AS $function$
DECLARE expiry_date date;
BEGIN
		expiry_date = (
		    select expiration_date from inventory.receiving_report_items where item = itemid
            order by created_date desc limit 1
		);
		RETURN expiry_date;
END; $function$
;
