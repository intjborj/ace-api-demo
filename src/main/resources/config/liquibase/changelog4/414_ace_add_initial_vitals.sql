ALTER TABLE pms.vital_signs ADD COLUMN note varchar default null;
ALTER TABLE pms.cases ADD COLUMN last_meal timestamp(6);