CREATE OR REPLACE view billing.doctors_pf as
select bi.id,
bi.billing,
b.billing_no,
b.patient,
concat(p.last_name,', ',p.first_name,' ', p.middle_name,' ',p.name_suffix) as pt_fullname,
b.patient_case,
c.case_no,
bi.record_no,
bi.description,
bi.created_by,
bi.created_date,
bi.department,
bi.item_type,
bi.debit as pf_gross,
bi.transaction_date,
bid.field_value as pf_vat_amount,
bid2.field_value as pf_vat_rate,
bid3.field_value as pf_wtx_amount,
bid4.field_value as pf_wtx_rate,
bid5.field_value as pf_net,
bid6.field_value as pf_employee_id
from billing.billing_item bi
left join billing.billing_item_details bid on bid.billingitem = bi.id and bid.field_name = 'PF_VAT_AMT'
left join billing.billing_item_details bid2 on bid2.billingitem = bi.id and bid2.field_name = 'PF_VAT_RATE'
left join billing.billing_item_details bid3 on bid3.billingitem = bi.id and bid3.field_name = 'PF_WTX_AMT'
left join billing.billing_item_details bid4 on bid4.billingitem = bi.id and bid4.field_name = 'PF_WTX_RATE'
left join billing.billing_item_details bid5 on bid5.billingitem = bi.id and bid5.field_name = 'PF_NET'
left join billing.billing_item_details bid6 on bid6.billingitem = bi.id and bid6.field_name = 'PF_EMPLOYEEID'
left join billing.billing b on b.id = bi.billing
left join pms.patients p on p.id = b.patient
left join pms.cases c  on c.id = b.patient_case
where bi.item_type in ('PF') and bi.status = 'ACTIVE' order by transaction_date asc;

--------------------------------------------------------
CREATE OR REPLACE VIEW billing.billingitem_simpleservice_view_with_department
AS SELECT bid.billingitem,
    bid.field_value,
    svc.process_code,
    svc.servicename AS service,
    svc.department AS svc_department,
    svc.service_code,
    svc.description AS service_description
   FROM billing.billing_item_details bid
     LEFT JOIN ancillary.services svc ON svc.id = bid.field_value::uuid
  WHERE bid.field_name::text = 'SERVICEID'::text;
 -------------------------------------------------------
 CREATE OR REPLACE VIEW billing.billingitem_service_view_with_department
AS SELECT bid.billingitem,
    bid.field_value,
    svc.process_code,
    svc.servicename AS service,
    dept.id AS svc_department,
    dept.parent_department AS svc_parentdepartment,
    svc.service_code,
    svc.description as service_description
   FROM billing.billing_item_details bid
     LEFT JOIN ancillary.services svc ON svc.id = bid.field_value::uuid
     LEFT JOIN departments dept ON dept.id = svc.department
  WHERE bid.field_name::text = 'SERVICEID'::text;
  -----------------------------------------
  CREATE OR REPLACE VIEW billing.billingitem_service_view
AS SELECT bid.billingitem,
    bid.field_value,
    svc.process_code,
    svc.servicename AS service,
    svc.service_code,
    svc.description as service_description
   FROM billing.billing_item_details bid
     LEFT JOIN ancillary.services svc ON svc.id = bid.field_value::uuid
  WHERE bid.field_name::text = 'SERVICEID'::text;
