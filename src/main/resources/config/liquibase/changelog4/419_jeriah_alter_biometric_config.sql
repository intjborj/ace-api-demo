ALTER TABLE hrm.employee_biometric_config
  ADD CONSTRAINT unique_biometric_config_id
  UNIQUE (biometric_no, employee_id, biometric_id);