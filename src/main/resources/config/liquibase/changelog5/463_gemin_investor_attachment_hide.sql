
ALTER TABLE billing.investor_attachments
    ADD COLUMN hide                         bool not null default false;
