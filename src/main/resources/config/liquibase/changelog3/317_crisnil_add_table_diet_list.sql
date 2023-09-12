create table dietary.patient_diet_list
(
    id uuid
        constraint patient_diet_list_pk
            primary key,
    patient_case uuid,
    meal_sched varchar(20),
    prepared_by uuid,
    served_by uuid,
    served timestamp,
    prepared timestamp,
    status varchar(10),
    diet_type varchar(50),
    requested_from uuid,
    requested_by uuid,
    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP
);
