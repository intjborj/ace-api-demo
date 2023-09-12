CREATE TABLE hrm.biometric_service_config
(
    id uuid NOT NULL,
    ip_address character varying(20),
    port character varying(20),

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        boolean,
    PRIMARY KEY (id)
);