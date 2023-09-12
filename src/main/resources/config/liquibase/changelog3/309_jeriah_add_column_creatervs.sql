ALTER TABLE doh.create_rvs_account
ADD COLUMN submitted_date_time timestamp(6) default CURRENT_TIMESTAMP;