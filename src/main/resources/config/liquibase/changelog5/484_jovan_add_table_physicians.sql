CREATE TABLE IF NOT EXISTS hospital_configuration.physicians(
    id UUID,
    first_name    varchar,
    middle_name   varchar NULL,
    last_name   varchar,
    suffix      varchar NULL,
    title      varchar,
    general_practitioner bool NOT NULL,
    specialization      uuid constraint fk_physicians_specialization
                        references hospital_configuration.specialties(id) on update cascade on delete restrict,



    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);