CREATE OR REPLACE FUNCTION inventory.on_hand_last_wcost(dep uuid, itemId uuid, trans_date date)
 RETURNS TABLE(onhand integer,last_wcost numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  select inventory.onhand_by_date(dep,itemId,trans_date) as onhand, 
	inventory.last_wcost_by_date(itemId, trans_date) as last_wcost;
END;
$function$
;