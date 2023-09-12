ALTER TABLE hrm.shifts
  ADD COLUMN created_by         varchar(50),
  ADD COLUMN created_date       timestamp(6) default CURRENT_TIMESTAMP,
  ADD COLUMN last_modified_by   varchar(50),
  ADD COLUMN last_modified_date timestamp(6) default CURRENT_TIMESTAMP;