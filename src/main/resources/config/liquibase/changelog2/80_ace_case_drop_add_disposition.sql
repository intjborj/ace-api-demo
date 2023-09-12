-- some errors on discharge disposition appeared at this point in time
-- this is a fix for it.
alter table pms.cases
    drop column if exists discharge_disposition,
	add discharge_disposition varchar(255);
