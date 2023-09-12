alter table hospital_configuration.constants
  add constraint fk_constant_type foreign key (type)
    references hospital_configuration.constant_types
    on update cascade on delete restrict;
