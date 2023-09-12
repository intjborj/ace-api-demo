INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('4f847031-569b-4f7e-a05e-fc993bd0ec71', 'EXECUTED EVENTS', 'EXECUTED EVENTS', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'Doctor orders a patient diet', 'dietary_order_executed', 'Doctor orders a patient diet',
        '4f847031-569b-4f7e-a05e-fc993bd0ec71', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'When a patient is admitted', 'admission_order_executed', 'When a patient is admitted',
        '4f847031-569b-4f7e-a05e-fc993bd0ec71', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'When a patient has been allowed to go home', 'maygohome_order_executed',
        'When a patient has been allowed to go home', '4f847031-569b-4f7e-a05e-fc993bd0ec71', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'When discharge orders has been deferred', 'maygohome_order_deferred',
        'When discharge orders has been deferred', '4f847031-569b-4f7e-a05e-fc993bd0ec71', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
