drop table if exists "accounting"."chart_of_accounts";
create table accounting.chart_of_accounts
(
    id                             uuid NOT NULL,
    account_code                   varchar NULL,
    description                    varchar NULL,
    category                       boolean NULL,
    tags                           varchar NULL,
    parent                         uuid NULL,
    deprecated                     bool NULL,

    created_by                     varchar(50) NULL,
    created_date                   timestamp NULL DEFAULT now(),
    last_modified_by               varchar(50) NULL,
    last_modified_date             timestamp NULL DEFAULT now(),
    deleted                        bool,

    CONSTRAINT chartofaccounts_pkey PRIMARY KEY (id)

);