INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('00e29ef8-01c5-4c4b-917a-bfb01d68400c', 'REGISTRY TYPES', 'REGISTRY TYPES', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'ER-PATIENT', 'ERD', 'ERD',
        '00e29ef8-01c5-4c4b-917a-bfb01d68400c', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'OUT-PATIENT', 'OPD', 'OPD',
        '00e29ef8-01c5-4c4b-917a-bfb01d68400c', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'IN-PATIENT', 'IPD', 'IPD',
        '00e29ef8-01c5-4c4b-917a-bfb01d68400c', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'FOR-ADMISSION', 'ADM', 'ADM',
        '00e29ef8-01c5-4c4b-917a-bfb01d68400c', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
