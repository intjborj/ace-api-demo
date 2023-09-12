alter table pms.intakes
  add column remarks text;

alter table pms.outputs
  add column remarks text,
  add column drainage text;
