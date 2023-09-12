ALTER TABLE pms.cases
  ADD COLUMN vap_infection bool NULL,
  ADD COLUMN vap_infected_date TIMESTAMP;