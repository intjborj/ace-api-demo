CREATE TABLE dietary.diets
(
    id uuid NOT NULL,
    diet_name character varying,
    diet_description character varying,
    PRIMARY KEY (id)
);

ALTER TABLE pms.cases
    ADD COLUMN diet uuid;
