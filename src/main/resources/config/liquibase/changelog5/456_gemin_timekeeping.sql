create table hrm.timekeepings
(
  id                             uuid not null primary key,
  title                          varchar,
  date_start                     timestamp,
  date_end                       timestamp,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);


create table hrm.timekeeping_employees
(
  id                             uuid not null primary key,
  timekeeping                    uuid constraint fk_timekeeping_employees_timekeeping_id
                                      references hrm.timekeepings(id)
                                      on update cascade on delete restrict,
  employee                       uuid constraint fk_timekeeping_employees_employee_id
                                      references hrm.employees(id)
                                      on update cascade on delete restrict,
  status                         varchar,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);

create table hrm.accumulated_logs
(
  id                                                            uuid not null primary key,
  timekeeping_employee                                          uuid constraint fk_accumulated_logs_timekeeping_employee_id
                                                                    references hrm.timekeeping_employees(id)
                                                                    on update cascade on delete restrict,
  status                                                        varchar,
  with_hazard_pay	                                            bool,
  "date"	                                                    timestamp,
  in_time	                                                    timestamp,
  out_time	                                                    timestamp,
  schedule_end	                                                timestamp,
  schedule_start	                                            timestamp,
  is_rest_day	                                                bool,
  message	                                                    varchar,
  undertime	                                                    numeric,
  late	                                                        numeric,
  hours_absent	                                                numeric,
  worked	                                                    numeric,
  hours_regular_overtime	                                    numeric,
  hours_worked_nsd	                                            numeric,
  worked_oic	                                                numeric,
  hours_regular_oic_overtime	                                numeric,
  hours_worked_oic_nsd	                                        numeric,
  hours_rest_day	                                            numeric,
  hours_rest_day_nsd	                                        numeric,
  hours_rest_overtime	                                        numeric,
  hours_double_holiday	                                        numeric,
  hours_double_holiday_nsd	                                    numeric,
  hours_double_holiday_overtime	                                numeric,
  hours_double_holiday_oic	                                    numeric,
  hours_double_holiday_oic_nsd	                                numeric,
  hours_double_holiday_oic_overtime	                            numeric,
  hours_double_holiday_and_rest_day	                            numeric,
  hours_double_holiday_and_rest_day_overtime	                numeric,
  hours_double_holiday_and_rest_day_nsd	                        numeric,
  hours_regular_holiday	                                        numeric,
  hours_regular_holiday_overtime	                            numeric,
  hours_regular_holiday_nsd	                                    numeric,
  hours_regular_holiday_oic	                                    numeric,
  hours_regular_holiday_oic_overtime	                        numeric,
  hours_regular_holiday_oic_nsd	                                numeric,
  hours_regular_holiday_and_rest_day	                        numeric,
  hours_regular_holiday_and_rest_day_overtime	                numeric,
  hours_regular_holiday_and_rest_day_nsd	                    numeric,
  hours_special_holiday	                                        numeric,
  hours_special_holiday_overtime	                            numeric,
  hours_special_holiday_nsd	                                    numeric,
  hours_special_holiday_oic	                                    numeric,
  hours_special_holiday_oic_overtime	                        numeric,
  hours_special_holiday_oic_nsd	                                numeric,
  hours_special_holiday_and_rest_day	                        numeric,
  hours_special_holiday_and_rest_day_overtime	                numeric,
  hours_special_holiday_and_rest_day_nsd	                    numeric,
  original_worked	                                            numeric,
  original_hours_regular_overtime	                            numeric,
  original_hours_worked_nsd	                                    numeric,
  original_worked_oic	                                        numeric,
  original_hours_regular_oic_overtime	                        numeric,
  original_hours_worked_oic_nsd	                                numeric,
  original_hours_rest_day	                                    numeric,
  original_hours_rest_day_nsd	                                numeric,
  original_hours_rest_overtime	                                numeric,
  original_hours_double_holiday	                                numeric,
  original_hours_double_holiday_nsd	                            numeric,
  original_hours_double_holiday_overtime	                    numeric,
  original_hours_double_holiday_oic	                            numeric,
  original_hours_double_holiday_oic_nsd	                        numeric,
  original_hours_double_holiday_oic_overtime	                numeric,
  original_hours_double_holiday_and_rest_day	                numeric,
  original_hours_double_holiday_and_rest_day_nsd	            numeric,
  original_hours_double_holiday_and_rest_day_overtime	        numeric,
  original_hours_regular_holiday	                            numeric,
  original_hours_regular_holiday_nsd	                        numeric,
  original_hours_regular_holiday_overtime	                    numeric,
  original_hours_regular_holiday_oic	                        numeric,
  original_hours_regular_holiday_oic_nsd	                    numeric,
  original_hours_regular_holiday_oic_overtime	                numeric,
  original_hours_regular_holiday_and_rest_day	                numeric,
  original_hours_regular_holiday_and_rest_day_nsd	            numeric,
  original_hours_regular_holiday_and_rest_day_overtime	        numeric,
  original_hours_special_holiday	                            numeric,
  original_hours_special_holiday_nsd	                        numeric,
  original_hours_special_holiday_overtime	                    numeric,
  original_hours_special_holiday_oic	                        numeric,
  original_hours_special_holiday_oic_nsd	                    numeric,
  original_hours_special_holiday_oic_overtime	                numeric,
  original_hours_special_holiday_and_rest_day	                numeric,
  original_hours_special_holiday_and_rest_day_nsd	            numeric,
  original_hours_special_holiday_and_rest_day_overtime	        numeric,

  deleted                        BOOL,
  deleted_date                   timestamp(6) default CURRENT_TIMESTAMP,
  created_by                     varchar(50),
  created_date                   timestamp(6) default CURRENT_TIMESTAMP,
  last_modified_by               varchar(50),
  last_modified_date             timestamp(6) default CURRENT_TIMESTAMP
);





