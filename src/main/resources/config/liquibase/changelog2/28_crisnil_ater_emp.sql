alter table hrm.employees alter column prc_expiry_date type date using prc_expiry_date::date;

alter table hrm.employees alter column service_class type date using service_class::date;
