

CREATE TABLE accounting.fiscals (
                                    id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                    fiscal_id varchar NULL,
                                    from_date date NULL,
                                    to_date date NULL,
                                    remarks varchar NULL,
                                    active boolean NULL,
                                    CONSTRAINT fiscals_pkey PRIMARY KEY (id)
);


ALTER TABLE "accounting"."fiscals"
    ADD COLUMN "created_by" varchar NULL ,
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;





CREATE TABLE accounting.header_ledger (
                                         id uuid NOT NULL,
                                         particulars varchar NULL,
                                         doctype varchar NULL,
                                         docnum varchar NULL,
                                         fiscal uuid NULL,
                                         transaction_date timestamp NULL,
                                         journal_type varchar NULL,
                                         custom bool NULL,
                                         parent_ledger uuid NULL,
                                         beginning_balance bool NULL,
                                         CONSTRAINT ledgerheader_pkey PRIMARY KEY (id),
                                         CONSTRAINT fk1_header_ledger FOREIGN KEY (fiscal) REFERENCES accounting.fiscals(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

CREATE INDEX idx_ledgerheaderorigdate ON accounting.header_ledger USING btree (transaction_date);
CREATE INDEX idxlhfiscal ON accounting.header_ledger USING btree (fiscal);
CREATE INDEX idxparentledger ON accounting.header_ledger USING btree (parent_ledger);

ALTER TABLE "accounting"."header_ledger"
    ADD COLUMN "created_by" varchar NULL ,
    ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
    ADD COLUMN	"last_modified_by" varchar COLLATE "default",
    ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;



CREATE TABLE accounting.ledger (
                                           id uuid NOT NULL DEFAULT uuid_generate_v4(),
                                           chart_of_account uuid NULL,
                                           credit numeric(15,2) NULL,
                                           debit numeric(15,2) NULL,
                                           particulars varchar NULL,
                                           header uuid NULL,
                                           CONSTRAINT general_ledger_pkey PRIMARY KEY (id),
                                           CONSTRAINT fk_ledger_chartofaccount FOREIGN KEY (chart_of_account) REFERENCES accounting.chart_of_accounts(id) ON UPDATE CASCADE ON DELETE RESTRICT,
                                           CONSTRAINT fk_ledger_header FOREIGN KEY (header) REFERENCES accounting.header_ledger(id) ON UPDATE CASCADE ON DELETE SET NULL
);

CREATE TABLE accounting.ledger_details (
                                                   id uuid NULL DEFAULT uuid_generate_v4(),
                                                   field_name varchar NULL,
                                                   field_value varchar NULL,
                                                   general_ledger uuid NULL,
                                                   CONSTRAINT fk_general_ledger_details_ledger FOREIGN KEY (general_ledger) REFERENCES accounting.ledger(id) ON UPDATE CASCADE ON DELETE CASCADE
);

CREATE INDEX general_ledger_gregg_idx ON accounting.ledger_details USING btree (field_name, field_value, general_ledger);
CREATE INDEX idx_field_name_genledger ON accounting.ledger_details USING btree (field_name, general_ledger);
CREATE INDEX idx_field_name_value ON accounting.ledger_details USING btree (field_name, field_value);
CREATE INDEX idx_general_ledger ON accounting.ledger_details USING btree (general_ledger);
CREATE INDEX idx_gl_field_name ON accounting.ledger_details USING btree (field_name);
CREATE INDEX idx_gl_field_value ON accounting.ledger_details USING btree (field_value);



ALTER TABLE "accounting"."ledger"
    ADD COLUMN "created_by" varchar NULL ,
    ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
    ADD COLUMN	"last_modified_by" varchar COLLATE "default",
    ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;

