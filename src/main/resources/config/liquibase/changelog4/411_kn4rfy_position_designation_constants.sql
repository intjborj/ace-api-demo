INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('d3ad40b1-8530-4f45-927b-3b87f5097705', 'POSITION/DESIGNATION', 'POSITION/DESIGNATION', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'DERMATOLOGIST', 'DERMATOLOGIST', 'DERMATOLOGIST', 'd3ad40b1-8530-4f45-927b-3b87f5097705',
        'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'DENTIST', 'DENTIST', 'DENTIST', 'd3ad40b1-8530-4f45-927b-3b87f5097705', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'SPEECH THERAPIST', 'SPEECH THERAPIST', 'SPEECH THERAPIST',
        'd3ad40b1-8530-4f45-927b-3b87f5097705', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
