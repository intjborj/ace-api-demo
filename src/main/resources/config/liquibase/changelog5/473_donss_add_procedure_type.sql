ALTER TABLE ancillary.services
ADD COLUMN IF NOT EXISTS test_procedure_type int;

CREATE TABLE IF NOT EXISTS doh.test_procedure_type(
    code int not null primary key,
    description varchar,
    group_code int,

    created_by                     varchar(50),
    created_date                   timestamp(6) default CURRENT_TIMESTAMP,
    last_modified_by               varchar(50),
    last_modified_date             timestamp(6) default CURRENT_TIMESTAMP,
    deleted                        bool
);

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 1,'X-Ray',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=1
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 2,'Ultrasound',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=2
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 3,'CT-Scan',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=3
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 4,'MRI',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=4
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 5,'Mammography',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=5
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 6,'Angiography',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=6
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 7,'Linear Accelerator',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=7
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 8,'Dental X-Ray',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=8
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 9,'Others',1
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=9
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 10,'Urinalysis',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=10
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 11,'Fecalysis',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=11
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 12,'Hematology',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=12
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 13,'Clinical chemistry',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=13
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 14,'Immunology/Serology/HIV',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=14
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 15,'Microbiology (Smears/Culture & Sensitivity)',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=15
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 16,'Surgical Pathology',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=16
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 17,'Autopsy',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=17
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 18,'Cytology',2
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=18
    );

INSERT INTO doh.test_procedure_type(code, description, group_code)
    SELECT 19,'Number of Blood units Transfused',3
    WHERE NOT EXISTS (
        SELECT 1 FROM doh.test_procedure_type WHERE code=19
    );


