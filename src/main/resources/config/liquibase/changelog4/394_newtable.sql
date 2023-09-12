CREATE TABLE hrm.biometric_device
(
    id uuid NOT NULL,
    device_name character varying,
    ip_address character varying NOT NULL,
    port character varying NOT NULL,
    device_username character varying,
    device_password character varying,
    deleted bool,
    created_by         varchar(50),
    created_date       timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by   varchar(50),
    last_modified_date timestamp(6) default CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
);