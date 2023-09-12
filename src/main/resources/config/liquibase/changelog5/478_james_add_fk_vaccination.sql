ALTER TABLE "pms"."patient_vaccinations"
    ADD CONSTRAINT "fk_patient"
    FOREIGN KEY ("patient")
    REFERENCES pms.patients(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;

ALTER TABLE "pms"."patient_vaccinations"
    ADD CONSTRAINT  "fk_case_id"
    FOREIGN KEY ("case_id")
    REFERENCES pms.cases(id)
    ON DELETE SET NULL
    ON UPDATE CASCADE;
