CREATE OR REPLACE VIEW accounting.account_receivable_billsched as
 select * from accounting.ar_group bd where bd.field_name = 'BILLING_SCHEDULE_ID';

CREATE OR REPLACE VIEW accounting.account_receivable_company as
select * from accounting.ar_group bd where bd.field_name = 'COMPANY_ACCOUNT_ID';
