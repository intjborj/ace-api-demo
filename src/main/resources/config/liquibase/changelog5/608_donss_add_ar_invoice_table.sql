CREATE TABLE IF NOT EXISTS accounting.ar_invoice(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    invoice_no              varchar,
    ar_customers            uuid,
    due_date                date,
    invoice_date            date,
    invoice_type            varchar,
    discount_amount         numeric,
    is_CWT                  bool default false,
    is_vatable              bool default false,
    cwt_amount              numeric,
    vat_amount              numeric,
    total_hci_amount        numeric,
    total_pf_amount         numeric,
    total_amount_due        numeric,
    total_credit_note       numeric,
    total_payments          numeric,
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

CREATE INDEX ar_invoice_customer_index ON accounting.ar_invoice USING btree (ar_customers);
CREATE INDEX ar_invoice_deleted_index ON accounting.ar_invoice USING btree (deleted);

CREATE TABLE IF NOT EXISTS accounting.ar_invoice_items(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    record_no               varchar,
    invoice_no              varchar,
    ar_invoice_id           uuid,
    ar_customers            uuid,
    item_name               varchar,
    description             varchar,
    item_type               varchar,
    unit_price              numeric,
    quantity                int,
    discount                numeric,
    discount_amount         numeric,
    is_CWT                  bool default false,
    is_vatable              bool default false,
    cwt_amount              numeric,
    vat_amount              numeric,
    total_hci_amount        numeric,
    total_pf_amount         numeric,
    total_amount_due        numeric,
    credit_note             numeric,
    payment                 numeric,
    claims_item             boolean,
    patient_name            varchar,
    billing_no              varchar,
    soa_no                  varchar,
    approval_code           varchar,
    admission_date          date,
    discharge_date          date,
    registry_type           varchar,
    claims_details          jsonb,
    billing_item_id          uuid,
    billing_id               uuid,
    patient_id               uuid,
    case_id                  uuid,
    pf_name                  varchar,
    pf_id                    uuid,
    status                   varchar,
    reference_transfer_id	 uuid,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);

CREATE INDEX ar_invoice_items_customer_index ON accounting.ar_invoice_items USING btree (ar_customers);
CREATE INDEX ar_invoice_items_invoice_index ON accounting.ar_invoice_items USING btree (ar_invoice_id);
CREATE INDEX ar_invoice_items_billing_item_id_index ON accounting.ar_invoice_items USING btree (billing_item_id);
CREATE INDEX ar_invoice_items_billing_id_index ON accounting.ar_invoice_items USING btree (billing_id);
CREATE INDEX ar_invoice_items_patient_id_index ON accounting.ar_invoice_items USING btree (patient_id);
CREATE INDEX ar_invoice_items_case_id_index ON accounting.ar_invoice_items USING btree (case_id);
CREATE INDEX ar_invoice_items_pf_id_index ON accounting.ar_invoice_items USING btree (pf_id);
CREATE INDEX ar_invoice_items_reference_transfer_id_index ON accounting.ar_invoice_items USING btree (reference_transfer_id);