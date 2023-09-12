ALTER TABLE appointment.config ADD COLUMN default_max_person int default 30,
ADD COLUMN allowed_stat bool default true,
ADD COLUMN default_max_stat int default 5;