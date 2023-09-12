
CREATE VIEW "ancillary"."orderslip_view_new" AS

SELECT  os.id,
       os.orderslip_no,
       cs.id as CASE,
       p.id as Patient,
    (SELECT  id  from "ancillary".orderslip_item ost WHERE ost.status = 'NEW' and ost.orderslip = os.id ORDER BY ost.created_date desc limit 1 ) as Item,
    os.requesting_physician_name
    FROM "ancillary".orderslips os
    LEFT JOIN   pms.cases cs  ON (cs.id = os.case_number)
    LEFT JOIN pms.patients p ON (p.id = cs.patient)
