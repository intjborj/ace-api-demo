 CREATE TABLE hrm.employee_biometric_config (
    id uuid                                   PRIMARY KEY  DEFAULT uuid_generate_v4(),
    biometric_id    integer,
    biometric_no    decimal,
    employee_id UUID   not null
                                              constraint fk_employee_biometric_config_employee_id
                                              references hrm.employees(id)
                                              on update cascade on delete restrict,

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted bool
 );
