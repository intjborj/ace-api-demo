alter table hrm.employee_attendance
drop column if exists hide;

alter table hrm.employee_attendance
add column is_manual                 bool;

alter table hrm.employee_attendance
add column is_ignored            bool;

alter table hrm.employee_attendance
add column method                    varchar default null;