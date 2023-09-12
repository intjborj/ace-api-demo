alter table hrm.employees alter column prc_expiry_date type timestamp using prc_expiry_date::timestamp without time zone;
alter table hrm.employees alter column phic_expiry_date type timestamp using phic_expiry_date::timestamp without time zone;
alter table hrm.employees alter column service_class type varchar(100) using service_class::varchar(100);
