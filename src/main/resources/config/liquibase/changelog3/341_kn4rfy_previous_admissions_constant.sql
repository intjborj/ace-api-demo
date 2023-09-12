INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('1c9dbac1-3812-41ea-84fd-4e67129b64a7', 'PREVIOUS ADMISSIONS', 'PREVIOUS ADMISSIONS', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'NONE', 'NONE', 'NONE',
        '1c9dbac1-3812-41ea-84fd-4e67129b64a7', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'ACEMC BOHOL', 'ACEMC BOHOL', 'ACEMC BOHOL',
        '1c9dbac1-3812-41ea-84fd-4e67129b64a7', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'OTHER/TRANSFEREE', 'OTHER/TRANSFEREE', 'OTHER/TRANSFEREE',
        '1c9dbac1-3812-41ea-84fd-4e67129b64a7', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
