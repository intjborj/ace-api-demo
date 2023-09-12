CREATE TABLE IF NOT EXISTS accounting.ar_credit_note(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    credit_note_no              varchar,
    ar_customers                uuid,
    credit_note_date            date,
    credit_note_type            varchar,
    discount_percentage         numeric,
    discount_amount             numeric,
    is_CWT                      bool,
    is_vatable                  bool,
    total_cwt_amount            numeric,
    total_vat_amount            numeric,
    total_hci_amount        numeric,
    total_pf_amount         numeric,
    total_amount_due        numeric,
    reference               varchar,
    notes                   text,
    status                  varchar,
    ledger_id               uuid,

    approved_by             uuid,
    approved_date           timestamp(6),

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);

CREATE INDEX ar_credit_note_approved_by_index ON accounting.ar_credit_note USING btree (approved_by);
CREATE INDEX ar_credit_note_ledger_id_index ON accounting.ar_credit_note USING btree (ledger_id);
CREATE INDEX ar_credit_note_customer_index ON accounting.ar_credit_note USING btree (ar_customers);
CREATE INDEX ar_credit_note_deleted_index ON accounting.ar_credit_note USING btree (deleted);

CREATE TABLE IF NOT EXISTS accounting.ar_credit_note_items(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    record_no               varchar,
    credit_note_no          varchar,
    ar_credit_note_id       uuid,
    ar_customers            uuid,
    ar_invoice              uuid,
    recipient_customer      uuid,
    discount_department     uuid,
    ar_invoice_no           varchar,
    ar_invoice_item_id      uuid,
    ar_invoice_item_record_no      varchar,
    item_name               varchar,
    description             varchar,
    item_type               varchar,
    unit_price              numeric,
    quantity                int,
    discount_percentage     numeric,
    discount_amount         numeric,
    is_CWT                  bool,
    is_vatable              bool,
    total_cwt_amount        numeric,
    total_vat_amount        numeric,
    total_hci_amount        numeric,
    total_pf_amount         numeric,
    total_amount_due        numeric,
    claims_item             boolean,
    patient_name            varchar,
    patient_id              uuid,
    approval_code           varchar,
    pf_name                  varchar,
    pf_id                    uuid,
    recipient_invoice        uuid,
    reference                varchar,
    status                   varchar,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);


CREATE INDEX ar_credit_note_items_customer_index ON accounting.ar_credit_note_items USING btree (ar_customers);
CREATE INDEX ar_credit_note_items_invoice_index ON accounting.ar_credit_note_items USING btree (ar_invoice);
CREATE INDEX ar_credit_note_items_invoice_item_index ON accounting.ar_credit_note_items USING btree (ar_invoice_item_id);
CREATE INDEX ar_credit_note_items_credit_note_index ON accounting.ar_credit_note_items USING btree (ar_credit_note_id);
CREATE INDEX ar_credit_note_items_patient_id_index ON accounting.ar_credit_note_items USING btree (patient_id);
CREATE INDEX ar_credit_note_items_pf_id_index ON accounting.ar_credit_note_items USING btree (pf_id);
CREATE INDEX ar_credit_note_items_recipient_invoice_index ON accounting.ar_credit_note_items USING btree (recipient_invoice);
