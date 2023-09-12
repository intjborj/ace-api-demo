CREATE OR REPLACE FUNCTION inventory.on_hand_last_wcost_phy_id(dep uuid, itemId uuid, trans_date date, phyid uuid)
 RETURNS TABLE(onhand integer,last_wcost numeric, monthly_count bigint)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  select inventory.onhand_by_date(dep,itemId,trans_date) as onhand,
	inventory.last_wcost_by_date(itemId, trans_date) as last_wcost,
	(select vpc.monthly_count from inventory.view_physical_count vpc where vpc.id = phyid) as monthly_count;
END;
$function$
;