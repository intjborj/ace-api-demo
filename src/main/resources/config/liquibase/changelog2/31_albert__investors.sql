CREATE TABLE billing.investors (
                                       id uuid NOT NULL,
                                       firstname varchar NULL,
                                       middlename varchar NULL,
                                       lastname varchar NULL,
                                       address varchar NULL,
                                       patient_data uuid NULL,
                                       dependency uuid NULL,
                                       investor bool NULL DEFAULT true,
                                       deleted bool NULL,
                                       dob timestamp NULL,
                                       noofdaysleft numeric NULL,
                                       investor_no varchar NULL,
                                       "version" varchar NULL,
                                       datepaid date NULL,
                                       arno varchar NULL,
                                       suffix varchar NULL,

                                       created_by varchar(50) NULL,
                                       created_date timestamp NULL DEFAULT now(),
                                       last_modified_by varchar(50) NULL,
                                       last_modified_date timestamp NULL DEFAULT now(),

                                       CONSTRAINT investor_data_pkey PRIMARY KEY (id)
);

CREATE TABLE billing.investors_dependents (
                                             id uuid NOT NULL,
                                             firstname varchar NULL,
                                             middlename varchar NULL,
                                             lastname varchar NULL,
                                             relation_investor varchar NULL,
                                             dob timestamp NULL,
                                             deleted bool NULL,
                                             investor_data uuid NULL,
                                             "version" varchar NULL,
                                             "type" varchar NULL,
                                             investorid varchar NULL,
                                             suffix varchar NULL,
                                             created_by varchar NULL,
                                             created_date timestamp NULL,
                                             last_modified_by varchar NULL,
                                             last_modified_date timestamp NULL,

                                             CONSTRAINT investor_dependents_pkey PRIMARY KEY (id),
                                             CONSTRAINT fk_investor_dependents FOREIGN KEY (investor_data) REFERENCES billing.investors(id) ON UPDATE CASCADE ON DELETE CASCADE
);

