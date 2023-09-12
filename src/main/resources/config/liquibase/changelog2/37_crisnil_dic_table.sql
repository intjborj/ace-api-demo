create table ancillary.dic_number
(
	id uuid
		constraint dic_number_pk
			primary key,
	patient_id uuid,
  diagnostic_number varchar(100),
  department_id uuid,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
  deleted                        bool,


  FOREIGN KEY (patient_id) REFERENCES  pms.patients (id),
  FOREIGN KEY (department_id) REFERENCES  public .departments (id)
);

