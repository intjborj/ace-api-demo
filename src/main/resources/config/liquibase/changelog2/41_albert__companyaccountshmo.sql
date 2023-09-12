CREATE TABLE billing.companyaccounts (
                                            id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                            companyaccountid varchar NULL,
                                            companyname varchar NULL,
                                            referenceno varchar NULL,
                                            companyfulladdress varchar NULL,
                                            contactno varchar NULL,
                                            contactperson varchar NULL,
                                            remarks varchar NULL,
                                            created_by varchar(50) NULL,
                                            created_date timestamp NULL DEFAULT now(),
                                            last_modified_by varchar(50) NULL,
                                            last_modified_date timestamp NULL DEFAULT now(),
                                            tag varchar NULL,
                                            CONSTRAINT companyaccounts_pkey PRIMARY KEY (id)
);