create or replace view inventory.wcostref as select i.id,
COALESCE(( SELECT inventory.last_wcost(i.id) AS last_wcost), 0::numeric) AS last_wcost
 from inventory.item i where i.active = true;