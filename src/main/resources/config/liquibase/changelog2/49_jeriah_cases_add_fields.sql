-- auto-generated definition
alter table pms.cases
    add column is_infacility_delivery boolean,
    add column delivery_type varchar,
    add column is_antenatal boolean,
    add column is_postnatal boolean,
    add column discharge_condition varchar,
    add column time_of_death timestamp(6),
    add column is_dead_on_arrival boolean,
    add column death_type varchar,
    add column operation_code varchar;

alter table pms.patients
    add column age int;


alter table hrm.employees
    add column profession_designation varchar,
    add column employee_type varchar;



