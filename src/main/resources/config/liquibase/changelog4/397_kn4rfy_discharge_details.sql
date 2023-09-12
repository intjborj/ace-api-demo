alter table pms.cases
  add column refusal_of_admission varchar(300),
  add column death_expiration varchar(50);

UPDATE pms.cases
SET death_expiration = 'DEAD ON ARRIVAL'
WHERE is_dead_on_arrival = true;
