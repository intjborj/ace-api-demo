--------- expiry ref date
CREATE OR REPLACE FUNCTION inventory.expiry_ref(filterdate date)
 RETURNS TABLE(item uuid, expiration_date date)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  WITH expiry AS (
         SELECT a.item,
            last_value(a.expiration_date) OVER (PARTITION BY a.item ORDER BY a.created_date RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS expiration_date
           FROM inventory.receiving_report_items a
           where DATE(created_date) <= filterdate
          GROUP BY a.item, a.expiration_date, a.created_date
        )
 SELECT expiry.item,
    expiry.expiration_date
   FROM expiry
  GROUP BY expiry.item, expiry.expiration_date;
END;
$function$
;
--------- expiry ref date
--------- on hand ref date
CREATE OR REPLACE FUNCTION inventory.onhand_ref(filterdate date)
 RETURNS TABLE(source_dep uuid, item uuid, onhand bigint)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  SELECT a.source_dep,
    a.item,
    sum(a.ledger_qty_in - a.ledger_qty_out) AS onhand
   FROM inventory.inventory_ledger a
  WHERE a.is_include = true and  date(a.ledger_date) <= filterdate
  GROUP BY a.source_dep, a.item;
END;
$function$
;
---------on hand ref date 
---------unitcost ref date 
CREATE OR REPLACE FUNCTION inventory.unitcost_ref(filterdate date)
 RETURNS TABLE(item uuid, unitcost numeric)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  WITH samp AS (
         SELECT a.item,
            last_value(a.ledger_unit_cost) OVER (PARTITION BY a.item ORDER BY a.ledger_date RANGE BETWEEN UNBOUNDED PRECEDING AND UNBOUNDED FOLLOWING) AS unitcost
           FROM inventory.inventory_ledger a
          WHERE (a.document_types = ANY (ARRAY['254a07d3-e33a-491c-943e-b3fe6792c5fc'::uuid, '0caab388-e53b-4e94-b2ea-f8cc47df6431'::uuid, 'af7dc429-8352-4f09-b58c-26a0a490881c'::uuid, '27d236bb-c023-44dc-beac-18ddfe1daf79'::uuid, '37683c86-3038-4207-baf0-b51456fd7037'::uuid])) AND a.is_include = true
          -- SRR, BEG, EP, PHY, MP
          and date(a.ledger_date) <= filterdate
          GROUP BY a.item, a.ledger_unit_cost, a.ledger_date
        )
 SELECT samp.item,
    samp.unitcost
   FROM samp
  GROUP BY samp.item, samp.unitcost;
END;
$function$
;
---------unitcost ref date

CREATE OR REPLACE FUNCTION inventory.onhand_report(filter_search varchar, dep_id uuid, filterdate date)
 RETURNS TABLE(id uuid, item uuid, desc_long character varying, unit_description character varying, unit_of_usage character varying, category_description character varying,
 				department uuid, department_name character varying, onhand bigint , last_unit_cost numeric, last_wcost numeric, expiration_date date)
 LANGUAGE plpgsql
AS $function$
BEGIN
  RETURN QUERY
  SELECT
  		a.id,
		a.item,
		b.desc_long,
		c.unit_description as unit_of_purchase,
		d.unit_description as unit_of_usage,
		e.category_description ,
		a.department ,
		f.department_name,
		COALESCE(h.onhand,0) as onhand,
		COALESCE(i.unitcost,0) as last_unit_cost,
		COALESCE((SELECT inventory.last_wcost_by_date(a.item, filterdate)),0) as last_wcost,
		COALESCE(g.expiration_date,NULL) as expiration_date
	FROM
		inventory.department_item a
		LEFT join inventory.item b on a.item = b.id
		LEFT join inventory.unit_measurements c on b.unit_of_purchase = c.id
		LEFT join inventory.unit_measurements d on b.unit_of_usage = d.id
		LEFT join inventory.item_categories e on b.item_category = e.id
		LEFT join public.departments f on a.department  = f.id
		---------------functions----------------------------------
		LEFT join inventory.expiry_ref(filterdate) g on g.item  = a.item
		LEFT join inventory.onhand_ref(filterdate) h on h.item = a.item AND h.source_dep = a.department
		LEFT join inventory.unitcost_ref(filterdate) i on i.item  = a.item
		where lower(b.desc_long)  like lower(concat('%',filter_search,'%')) AND
		a.department = dep_id order by b.desc_long;
END;
$function$
;