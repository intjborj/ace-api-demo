alter table pms.patients
    add email_address varchar(20),
    add nationality varchar(50);

alter table hrm.employees
    add email_address varchar(20),
    add nationality varchar(50),
    add civil_status varchar(20),
    add withold_tax_rate numeric;

alter table pms.cases
    add time_of_birth time;