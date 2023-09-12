alter table departments
    add column can_clear_patient_discharge boolean,
    add column can_receive_items boolean,
    add column can_purchase_items boolean;