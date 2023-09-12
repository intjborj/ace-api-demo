alter table ancillary.rf_fees alter column fixed_value type numeric(6,2) using fixed_value::numeric(6,2);

alter table ancillary.rf_fees alter column fixed_value set default 0;
