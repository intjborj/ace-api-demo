INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('cbd017af-f6f3-4e1d-8f85-39397a3cc6f8', 'TRIAGE TYPES', 'TRIAGE TYPES', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), '1 - RESUSCITATION', '1', 'RESUSCITATION',
        'cbd017af-f6f3-4e1d-8f85-39397a3cc6f8', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), '2 - EMERGENCY', '2', 'EMERGENCY',
        'cbd017af-f6f3-4e1d-8f85-39397a3cc6f8', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), '3 - URGENT', '3', 'URGENT',
        'cbd017af-f6f3-4e1d-8f85-39397a3cc6f8', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), '4 - SEMI-URGENT', '4', 'SEMI-URGENT',
        'cbd017af-f6f3-4e1d-8f85-39397a3cc6f8', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), '5 - NON-URGENT', '5', 'NON-URGENT',
        'cbd017af-f6f3-4e1d-8f85-39397a3cc6f8', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
