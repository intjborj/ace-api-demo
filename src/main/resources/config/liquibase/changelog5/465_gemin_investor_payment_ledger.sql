

ALTER TABLE cashiering.payment_tracker
    ADD COLUMN investorid                         uuid;







--
--ALTER TABLE billing.investors
--    ADD COLUMN barangay                         varchar,
--    ADD COLUMN city                             varchar,
--    ADD COLUMN province                         varchar,
--    ADD COLUMN country                          varchar,
--    ADD COLUMN zip_code                         varchar,
--    ADD COLUMN gender                           varchar,
--    ADD COLUMN civil_status                     varchar,
--    ADD COLUMN place_of_birth                   varchar,
--    ADD COLUMN email_address                    varchar,
--    ADD COLUMN present_employer                 varchar,
--    ADD COLUMN educational_attainment           varchar,
--    ADD COLUMN profession                       varchar,
--    ADD COLUMN office_address                   varchar,
--    ADD COLUMN contact_numbers                  jsonb,
--    ADD COLUMN identifications                  jsonb,
--    ADD COLUMN referred_by                      uuid constraint fk_investors_employee_id
--                                                    references hrm.employees(id)
--                                                    on update cascade on delete restrict;
--
--
--ALTER TABLE billing.investors_dependents
--    ADD COLUMN use_investor_id                  boolean DEFAULT false;
--
--
--CREATE TABLE "billing"."investor_attachments" (
--    id                                                uuid NOT NULL PRIMARY KEY,
--    investor                                          uuid NOT NULL
--                                                           constraint fk_investor_attachments_investor_id
--                                                           references billing.investors(id)
--                                                           on update cascade on delete restrict,
--    dependent                                         uuid NOT NULL
--                                                           constraint fk_investor_attachments_dependent_id
--                                                           references billing.investors_dependents(id)
--                                                           on update cascade on delete restrict,
--    filename	                                      varchar,
--    description	                                      varchar,
--    mimetype	                                      varchar,
--    url_path	                                      varchar,
--
--    created_by                                        varchar(50),
--    created_date                                      timestamp DEFAULT now(),
--    last_modified_by                                  varchar(50),
--    last_modified_date                                timestamp DEFAULT now(),
--    deleted                                           bool
--);
--
--CREATE TABLE "billing"."investor_payment_ledger" (
--    id                                                uuid NOT NULL PRIMARY KEY,
--    investor                                          uuid NOT NULL
--                                                           constraint fk_payment_ledger_investor_id
--                                                           references billing.investors(id)
--                                                           on update cascade on delete restrict,
--    shift                                             uuid NOT NULL
--                                                           constraint fk_payment_ledger_shifting_id
--                                                           references cashiering.shifting(id)
--                                                           on update cascade on delete restrict,
--    date_received                                     timestamp,
--    payment_mode                                      varchar,
--    check_date                                        timestamp,
--    particular                                        varchar,
--    amount                                            numeric(15,2),
--    date_deposited                                    timestamp,
--    date_cleared                                      timestamp,
--    subscription_number                               varchar,
--    reference_number                                  varchar,
--    acquiring_bank                                    varchar,
--    depository_bank                                   varchar,
--    remarks                                           varchar,
--
--    created_by                                        varchar(50),
--    created_date                                      timestamp DEFAULT now(),
--    last_modified_by                                  varchar(50),
--    last_modified_date                                timestamp DEFAULT now(),
--    deleted                                           bool
--);
