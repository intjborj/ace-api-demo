ALTER TABLE pms.cases
   ADD CONSTRAINT "fk_vap_infection_by" FOREIGN KEY ("vap_infection_by") references hrm.employees(id) on update cascade on delete restrict,
   ADD CONSTRAINT "fk_bsi_infection_by" FOREIGN KEY ("bsi_infection_by") references hrm.employees(id) on update cascade on delete restrict,
   ADD CONSTRAINT "fk_uti_infection_by" FOREIGN KEY ("uti_infection_by") references hrm.employees(id) on update cascade on delete restrict;
