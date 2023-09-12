alter table billing.billing_item
    add rf_fee numeric default 0;

alter table billing.billing_item
    add rf_details varchar;

