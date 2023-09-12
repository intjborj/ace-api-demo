DROP TABLE IF EXISTS doh.configurations;
CREATE TABLE doh.configurations (
    id uuid NOT NULL primary key,
    total_deaths_config jsonb,

    created_by          varchar(50) NULL,
    created_date        timestamp NULL DEFAULT now(),
    last_modified_by    varchar(50) NULL,
    last_modified_date  timestamp NULL DEFAULT now(),
    deleted             bool NULL
);
