ALTER TABLE "accounting"."transaction_type"
  ADD COLUMN flag_value varchar default null,
  ADD COLUMN is_active varchar default true;
