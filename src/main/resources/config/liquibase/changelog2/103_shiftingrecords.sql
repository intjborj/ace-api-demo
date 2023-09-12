CREATE TABLE cashiering.shifting (
                                     id uuid NOT NULL,
                                     shiftno varchar NULL,
                                     cashier uuid NULL,
                                     active bool NULL,
                                     startshift timestamp NULL,
                                     endshift timestamp NULL,
                                     acknowledged bool NULL,
                                     acknowledgedate timestamp NULL,
                                     acknowledgeby varchar NULL,
                                     overage_shortage numeric(15,2) NULL,
                                     CONSTRAINT shifting_pkey PRIMARY KEY (id),
                                     CONSTRAINT fk_shifting_cashier FOREIGN KEY (cashier) REFERENCES cashiering.cashierterminals(id) ON UPDATE CASCADE ON DELETE RESTRICT
);

ALTER TABLE cashiering.shifting
ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;


CREATE TABLE cashiering.shifting_details (
                                             id uuid NULL DEFAULT uuid_generate_v4(),
                                             field_name varchar NULL,
                                             field_value varchar NULL,
                                             shifting uuid NULL,
                                             CONSTRAINT fk_shifting_shifting_details FOREIGN KEY (shifting) REFERENCES cashiering.shifting(id) ON UPDATE CASCADE ON DELETE CASCADE
);



CREATE TABLE cashiering.payment_tracker (
                                            id uuid NOT NULL,

                                            totalpayments numeric(15,2) NULL,
                                            total_cash numeric(15,2) NULL,
                                            total_check numeric(15,2) NULL,
                                            total_card numeric(15,2) NULL,
                                            total_deposit numeric(15,2) NULL,
                                            change numeric(15,2) NULL,
                                            hosp numeric(15,2) NULL,
                                            pf numeric(15,2) NULL,

                                            ornumber varchar NULL,
                                            completed bool NULL,
                                            description varchar NULL,
                                            billingid uuid NULL,
                                            shiftid uuid NULL,

                                            receipt_type varchar NULL, -- AR/OR
                                            voided bool NULL,
                                            void_date timestamp NULL,
                                            void_type varchar NULL, -- DATA
                                            CONSTRAINT payment_tracker_pkey PRIMARY KEY (id),
                                            CONSTRAINT fk_pt_shift FOREIGN KEY (shiftid) REFERENCES cashiering.shifting(id) ON UPDATE CASCADE ON DELETE SET NULL
);

ALTER TABLE cashiering.payment_tracker
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;











CREATE TABLE cashiering.payment_tracker_details (
                                                    id uuid NOT NULL,


                                                    amount numeric(15,2) NULL,

                                                    "type" varchar,
                                                    reference varchar,
                                                    check_date varchar,
                                                    expiry varchar,
                                                    bank varchar,
                                                    name_of_card varchar,
                                                    card_type varchar,
                                                    approval_code varchar,
                                                    pos_terminal_id varchar,

                                                    payment_tracker uuid NULL,
                                                    cleared bool NULL,
                                                    denied bool NULL,
                                                    cleareddate timestamp NULL,
                                                    denieddate timestamp NULL,
                                                    reconcile_id uuid NULL,
                                                    reconcile_date timestamp NULL,
                                                    bank_id uuid NULL,
                                                    CONSTRAINT payment_tracker_details_pkey PRIMARY KEY (id),
                                                    CONSTRAINT fk_check_ptracker FOREIGN KEY (payment_tracker) REFERENCES cashiering.payment_tracker(id) ON UPDATE CASCADE ON DELETE CASCADE
);

ALTER TABLE cashiering.payment_tracker_details
    ADD COLUMN "created_by" varchar NULL COLLATE "default",
ADD COLUMN	"created_date" timestamp  NULL DEFAULT current_timestamp,
ADD COLUMN	"last_modified_by" varchar COLLATE "default",
ADD COLUMN	"last_modified_date" timestamp NULL DEFAULT current_timestamp;
