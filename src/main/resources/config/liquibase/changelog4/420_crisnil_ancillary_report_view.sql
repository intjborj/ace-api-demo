drop view if exists "ancillary"."orderslip_item_report";
CREATE VIEW "ancillary"."orderslip_item_report" as
select s.servicename,d.department_name, count(1) FILTER ( WHERE c.registry_type = 'IPD' ) as INPATIENT,
                        SUM(b.debit)
                        FILTER ( WHERE c.registry_type = 'IPD' ) as INPATIENT_CHARGES,
       count(1) FILTER ( WHERE c.registry_type != 'IPD' ) as OPD,
       SUM(b.debit)
           FILTER ( WHERE c.registry_type != 'IPD' ) as OPD_CHARGES,
      SUM(b.debit) as TOTAL_Charges
from ancillary.orderslip_item osi
          left join billing.billing_item b on osi.billing_item = b.id
          left join ancillary.services s on osi."service" = s.id
          left join public.departments d on s.department = d.id
          left join ancillary.orderslips o on osi.orderslip = o.id
          left join pms.cases c on o.case_number = c.id
          left join pms.patients p on c.patient = p.id
where osi.billing_item is not null
GROUP BY s.servicename,d.department_name
