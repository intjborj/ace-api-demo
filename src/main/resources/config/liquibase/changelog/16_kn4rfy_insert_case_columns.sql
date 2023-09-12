-- auto-generated definition
alter table pms.cases
  add column room uuid,
  add column admission_date timestamp without time zone,
  add column attending_physician uuid,
  add column admitting_physician uuid;
