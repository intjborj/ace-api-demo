CREATE TABLE IF NOT EXISTS accounting.ar_transaction_ledger(
    id uuid PRIMARY KEY  DEFAULT uuid_generate_v4(),
    record_no                   varchar,
    ar_customers                uuid,
    ar_invoice_id               uuid,
    ar_credit_note_id           uuid,
    ar_payment_id               uuid,
    ledger_date                 timestamp(6) default CURRENT_TIMESTAMP,
    doc_date                    DATE,
    doc_type                    varchar,
    doc_no                      varchar,
    total_cwt_amount            numeric,
    total_vat_amount            numeric,
    total_hci_amount            numeric,
    total_pf_amount             numeric,
    total_amount_due            numeric,
    remaining_hci_balance       numeric,
    remaining_pf_balance        numeric,
    remaining_balance           numeric,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);

CREATE INDEX ar_transaction_ledger_ar_customers_index ON accounting.ar_transaction_ledger USING btree (ar_customers);
CREATE INDEX ar_transaction_ledger_ar_invoice_id_index ON accounting.ar_transaction_ledger USING btree (ar_invoice_id);
CREATE INDEX ar_transaction_ledger_ar_credit_note_id_index ON accounting.ar_transaction_ledger USING btree (ar_credit_note_id);
CREATE INDEX ar_transaction_ledger_ar_payment_id_index ON accounting.ar_transaction_ledger USING btree (ar_payment_id);
CREATE INDEX ar_transaction_ledger_deleted_index ON accounting.ar_transaction_ledger USING btree (deleted);

