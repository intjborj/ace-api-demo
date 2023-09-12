create  or replace  view accounting.all_receivable_items
as select
   ari.id as "ar_item_id",
   ari.account_receivable_id  as "ar_id",
   bi.id  as "billing_item_id",
   bi.billing  as "billing_id",
   ag.field_value as "account_id",
   ar.ar_no  as "ar_no",
   ari.record_no as "ar_record_no",
   ari.description as "ar_description",
   bi.description as "billing_description" ,
   ari.type,
   ari.amount,
   ari.debit,
   ari.credit,
   ari.created_by,
   ari.created_date,
   ari.last_modified_by,
   ari.last_modified_date
   from accounting.account_receivable_items ari
   left join accounting.account_receivable_items_details arid on arid.account_receivable_items  = ari.id  and arid.field_name  = 'BILLING_ITEM_ID'
   left join billing.billing_item bi on bi.id = arid.field_value::uuid
   left join accounting.account_receivable ar on ar.id = ari.account_receivable_id
   left join accounting.ar_group ag on ag.account_receivable  = ar.id and ag.field_name  = 'COMPANY_ACCOUNT_ID'
   where ag.field_value is not null and ar.status  = 'active'
   order by ar.ar_no