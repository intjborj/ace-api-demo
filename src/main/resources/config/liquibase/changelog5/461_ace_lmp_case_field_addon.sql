ALTER TABLE pms.obgyn_history
    ADD COLUMN term integer,
    ADD COLUMN preterm integer,
    ADD COLUMN age_of_gestation date,
    ADD COLUMN due_date date,
    ADD COLUMN last_menstrual_period date;