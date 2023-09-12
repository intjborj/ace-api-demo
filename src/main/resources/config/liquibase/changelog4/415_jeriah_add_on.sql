 CREATE TABLE hrm.add_on (
    id uuid                                   PRIMARY KEY  DEFAULT uuid_generate_v4(),
    add_on varchar,
    amount numeric,
    employee_id UUID   not null
                                              constraint fk_add_on_employee_id
                                              references hrm.employees(id)
                                              on update cascade on delete restrict,

    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    deleted bool
 );
