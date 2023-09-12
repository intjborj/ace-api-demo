CREATE SCHEMA accounting ;


CREATE TABLE accounting.bankaccounts (
                                         id uuid NOT NULL,
                                         bankaccountid varchar NULL,
                                         bankname varchar NULL,
                                         branch varchar NULL,
                                         bankaddress varchar NULL,
                                         accountname varchar NULL,
                                         accountnumber varchar NULL,
                                         remarks varchar NULL,
                                         created_by varchar(50) NULL,
                                         created_date timestamp NULL DEFAULT now(),
                                         last_modified_by varchar(50) NULL,
                                         last_modified_date timestamp NULL DEFAULT now(),
                                         CONSTRAINT bankaccounts_pkey PRIMARY KEY (id)
);

