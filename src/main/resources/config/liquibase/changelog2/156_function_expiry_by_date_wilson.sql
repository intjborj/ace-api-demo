CREATE OR REPLACE FUNCTION inventory.expiry_by_date(itemid uuid, filterdate date)
 RETURNS date
 LANGUAGE plpgsql
AS $function$
DECLARE expiry_date date;
BEGIN
		expiry_date = (
		    select expiration_date from inventory.receiving_report_items where item = itemid
            and DATE(created_date) <= filterdate
            order by created_date desc limit 1
		);
		RETURN expiry_date;
END; $function$
;
