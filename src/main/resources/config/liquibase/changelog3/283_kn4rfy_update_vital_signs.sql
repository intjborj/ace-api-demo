alter table pms.vital_signs
  alter column fetal_hr type varchar(10),
  alter column pain_score type varchar(10),
  add weight varchar(10),
  add cbs varchar(10);
