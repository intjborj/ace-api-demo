 ALTER TABLE hrm.employee_biometric_config
  DROP COLUMN  biometric_id;

  ALTER TABLE hrm.employee_biometric_config
  ADD COLUMN biometric_id  UUID     not null          constraint fk_employee_biometric_config_biometric_id
                                                      references hrm.biometric_device(id)
                                                      on update cascade on delete restrict;


