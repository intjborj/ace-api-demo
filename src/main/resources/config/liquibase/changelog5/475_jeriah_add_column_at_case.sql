ALTER TABLE pms.cases
  ADD COLUMN patient_transfer bool,
  ADD COLUMN previous_hci varchar;