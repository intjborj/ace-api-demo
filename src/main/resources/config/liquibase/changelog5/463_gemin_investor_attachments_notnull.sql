
ALTER TABLE billing.investor_attachments
    ALTER COLUMN investor drop not null,
    ALTER COLUMN dependent drop not null;
