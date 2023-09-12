 CREATE OR REPLACE FUNCTION inventory.onhand_report(filter_search character varying, dep_id uuid, filterdate date)
 RETURNS TABLE(id uuid, item uuid, desc_long character varying, unit_description character varying, unit_of_usage character varying, category_description character varying, department uuid, department_name character varying, onhand bigint, last_unit_cost numeric, last_wcost numeric, expiration_date date)
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
		LEFT join inventory.item b on a.item = b.id and b.active = true and (b.fix_asset is null or b.fix_asset = false)
		LEFT join inventory.unit_measurements c on b.unit_of_purchase = c.id
		LEFT join inventory.unit_measurements d on b.unit_of_usage = d.id
		LEFT join inventory.item_categories e on b.item_category = e.id
		LEFT join public.departments f on a.department  = f.id
		---------------functions----------------------------------
		LEFT join inventory.expiry_ref(filterdate) g on g.item  = a.item
		LEFT join inventory.onhand_ref(filterdate) h on h.item = a.item AND h.source_dep = a.department
		LEFT join inventory.unitcost_ref(filterdate) i on i.item  = a.item
		where lower(b.desc_long)  like lower(concat('%',filter_search,'%')) AND
		a.department = dep_id and a.is_assign = true and (b.consignment = false || b.consignment is null) order by b.desc_long;
END;
$function$
;