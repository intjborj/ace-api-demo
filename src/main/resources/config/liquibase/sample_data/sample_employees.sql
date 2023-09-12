INSERT INTO hrm.employees (id, employee_no, first_name, last_name, middle_name, name_suffix, address, country,
                           state_province, city_municipality, barangay, gender, dob, emergency_contact_name,
                           emergency_contact_address, emergency_contact_relationship, emergency_contact_no, "user",
                           created_by, created_date, last_modified_by, last_modified_date, department_of_duty, department)
VALUES ('47742949-d663-4ce8-b5df-039a56bd83b4', '1096', 'Admin', 'Section', 'Tan', 'III', '703 Starling Court',
        'United States', 'Pennsylvania', 'Philadelphia', 'Phoenix', 'Female', '1926-05-23', 'Lacy Gallehawk',
        '36621 Charing Cross Circle', 'Grandfather', '215-481-8734', 2, null, '2019-08-06 10:53:23.576659', null,
        '2019-08-06 10:53:23.576659', (select id from departments where department_name = 'CEO'),(select id from departments where department_name = 'CEO'));