-- FUNCTION: public.text_get_department_desc(uuid)

-- DROP FUNCTION public.text_get_department_desc(uuid);

CREATE OR REPLACE FUNCTION public.text_get_department_desc(
	department_id uuid)
    RETURNS text
    LANGUAGE 'plpgsql'

    COST 100
    VOLATILE

AS $BODY$
declare
department_desc text;
begin
select d.department_desc
into department_desc
from public.departments d
where d.id = department_id;
return department_desc;
end;
$BODY$;

-- View: ancillary.total_ancillary_service_by_parent_dept

-- DROP VIEW ancillary.total_ancillary_service_by_parent_dept;

CREATE OR REPLACE VIEW ancillary.total_ancillary_service_by_parent_dept
 AS
 SELECT d.parent_department AS id,
    text_get_department_desc(d.parent_department) AS department,
    count(*) AS count
   FROM ancillary.orderslip_item o
     LEFT JOIN ancillary.services s ON o.service = s.id
     JOIN departments d ON s.department = d.id
  WHERE o.status::text = 'COMPLETED'::text
  GROUP BY d.parent_department;


