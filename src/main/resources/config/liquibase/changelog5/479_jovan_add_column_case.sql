ALTER TABLE pms.cases
  ADD COLUMN bsi_infection bool NULL,
  ADD COLUMN bsi_infected_date TIMESTAMP,
   ADD COLUMN uti_infection bool NULL,
    ADD COLUMN uti_infected_date TIMESTAMP;