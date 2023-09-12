
alter table hrm.employee_attendance
add column original_type                 varchar;

UPDATE hrm.employee_attendance
set original_type = hrm.employee_attendance."type";
