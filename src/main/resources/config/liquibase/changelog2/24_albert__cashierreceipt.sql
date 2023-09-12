CREATE TABLE cashiering.receiptsissuance (
                                             id uuid NOT NULL,
                                             batchcode varchar NULL,
                                             receipt_from int8 NULL,
                                             receipt_to int8 NULL,
                                             receipt_current int8 NULL,
                                             terminal uuid NULL,
                                             created_by varchar(50) NULL,
                                             created_date timestamp NULL DEFAULT now(),
                                             last_modified_by varchar(50) NULL,
                                             last_modified_date timestamp NULL DEFAULT now(),
                                             activebatch bool NULL,
                                             arfrom int8 NULL,
                                             arto int8 NULL,
                                             arcurrent int8 NULL,
                                             aractive bool NULL,
                                             CONSTRAINT receiptsissuance_pkey PRIMARY KEY (id),
                                             CONSTRAINT fk_receipt_terminal FOREIGN KEY (terminal) REFERENCES cashiering.cashierterminals(id) ON UPDATE CASCADE ON DELETE RESTRICT
);