INSERT INTO hospital_configuration.constant_types (id, name, description, created_by, created_date, last_modified_by,
                                                   last_modified_date, deleted)
VALUES ('f8e4700b-2e3d-4cb0-a628-ae714262b517', 'DEATH TYPES', 'DEATH TYPES', 'admin',
        '2020-10-29 05:11:40.360000',
        'admin', '2020-10-29 05:11:40.360000', false);

INSERT INTO hospital_configuration.constants (id, name, value, short_code, type, created_by, created_date,
                                              last_modified_by, last_modified_date, deleted)
VALUES (uuid_generate_v4(), 'NEONATAL', 'NEONATAL', 'NEONATAL',
        'f8e4700b-2e3d-4cb0-a628-ae714262b517', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'MATERIAL', 'MATERIAL', 'MATERIAL',
        'f8e4700b-2e3d-4cb0-a628-ae714262b517', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false),
       (uuid_generate_v4(), 'NORMAL', 'NORMAL', 'NORMAL',
        'f8e4700b-2e3d-4cb0-a628-ae714262b517', 'admin',
        '2020-10-29 05:12:14.064000', 'admin', '2020-10-29 05:12:14.064000', false);
