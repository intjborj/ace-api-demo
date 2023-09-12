INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('eee7917a-82b7-46a8-8dd4-34de9bbb8438', 'LEAVE SCHEDULE TYPE', 'LEAVE SCHEDULE TYPE', 'admin',
        '2022-12-20 05:11:40.360000',
        'admin', '2022-12-20 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'Full Day Leave', '8', 'LEAVE', 'eee7917a-82b7-46a8-8dd4-34de9bbb8438',
        'admin',
        '2022-12-20 05:12:14.064000', 'admin', '2022-12-20 05:12:14.064000', false),
        (uuid_generate_v4(), 'Half Day Leave', '4', 'LEAVE_HALF', 'eee7917a-82b7-46a8-8dd4-34de9bbb8438',
         'admin',
         '2022-12-20 05:12:14.064000', 'admin', '2022-12-20 05:12:14.064000', false),
        (uuid_generate_v4(), 'Twelve Hour Leave', '12', 'TWELVE_HR_LEAVE', 'eee7917a-82b7-46a8-8dd4-34de9bbb8438',
         'admin',
         '2022-12-20 05:12:14.064000', 'admin', '2022-12-20 05:12:14.064000', false),
        (uuid_generate_v4(), 'Half Twelve Hour Leave', '6', 'TWELVE_HR_LEAVE_HALF', 'eee7917a-82b7-46a8-8dd4-34de9bbb8438',
         'admin',
         '2022-12-20 05:12:14.064000', 'admin', '2022-12-20 05:12:14.064000', false)
;