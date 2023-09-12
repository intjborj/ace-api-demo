ALTER TABLE accounting.account_receivable
ADD COLUMN transaction_date DATE;

ALTER TABLE "accounting"."billing_schedule"
ALTER COLUMN "transaction_date" TYPE date;