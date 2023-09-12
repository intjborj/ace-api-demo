ALTER TABLE hospital_configuration.physicians
DROP CONSTRAINT IF EXISTS physicians_pkey,
add CONSTRAINT physicians_pkey PRIMARY KEY (id);

ALTER TABLE hospital_configuration.rooms
DROP CONSTRAINT IF EXISTS rooms_pkey,
add CONSTRAINT rooms_pkey PRIMARY KEY (id);

ALTER TABLE hospital_configuration.clinics_doctors
DROP CONSTRAINT IF EXISTS clinics_doctors_pkey,
add CONSTRAINT clinics_doctors_pkey PRIMARY KEY (id);

ALTER TABLE pms.cases
    DROP CONSTRAINT IF EXISTS fk_opd_physician_physicians_id,
    DROP COLUMN IF EXISTS opd_physician;

ALTER TABLE pms.transfers
    DROP CONSTRAINT IF EXISTS fk_opd_physician_physicians_id,
    DROP COLUMN IF EXISTS opd_physician;

ALTER TABLE pms.cases
  ADD COLUMN opd_physician          UUID constraint fk_opd_physician_physicians_id
                                    references hospital_configuration.physicians(id)
                                    on update cascade on delete restrict;

ALTER TABLE pms.transfers
  ADD COLUMN opd_physician          UUID constraint fk_opd_physician_physicians_id
                                    references hospital_configuration.physicians(id)
                                    on update cascade on delete restrict;
