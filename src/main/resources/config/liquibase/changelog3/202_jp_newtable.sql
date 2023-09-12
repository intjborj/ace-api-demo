CREATE TABLE hospital_configuration.comlogik_settings
(
    id uuid NOT NULL,
    login character varying(255),
    password character varying(255),
    hos character varying(255),
    PRIMARY KEY (id)
)
WITH (
    OIDS = FALSE
);