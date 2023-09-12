CREATE TABLE billing.discounts (
                                      id uuid NOT NULL,
                                      code varchar NULL,
                                      discount varchar NULL,
                                      remarks varchar NULL,
                                      created_by varchar(50) NULL,
                                      created_date timestamp NULL DEFAULT now(),
                                      last_modified_by varchar(50) NULL,
                                      last_modified_date timestamp NULL DEFAULT now(),
                                      "type" varchar NULL,
                                      value numeric(15,2) NULL,
                                      active bool NULL,
                                      CONSTRAINT discounts_pkey PRIMARY KEY (id)
);


