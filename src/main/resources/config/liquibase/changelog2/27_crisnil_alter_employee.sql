alter table hrm.employees
	add bank_accnt_name varchar(50);

alter table hrm.employees
	add bank_accnt_no varchar(100);

alter table hrm.employees
	add prc_license_type varchar(100);

alter table hrm.employees
	add prc_license_no varchar(100);

alter table hrm.employees
	add prc_expiry_date varchar(100);

alter table hrm.employees
	add ptr_no varchar(100);

alter table hrm.employees
	add s2_no varchar(100);

alter table hrm.employees
	add phic_no varchar(100);

alter table hrm.employees
	add phic_group varchar(100);

alter table hrm.employees
	add phic_expiry_date varchar(100);

alter table hrm.employees
	add pmmc_no varchar(100);

alter table hrm.employees
	add service_class varchar(100);

alter table hrm.employees
	add specialization varchar(100);

alter table hrm.employees
	add service_type varchar(100);

alter table hrm.employees
	add vatable_or_non BOOLEAN default false;

alter table hrm.employees
	add pf_vat_rate numeric;

alter table hrm.employees
	add expanded_wtax_rate numeric;

alter table hrm.employees
	add rf_vat numeric;

alter table hrm.employees
	add care_provider_type varchar(100);

