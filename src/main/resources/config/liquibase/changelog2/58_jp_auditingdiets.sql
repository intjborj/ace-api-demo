ALTER TABLE dietary.diets
    ADD COLUMN created_by character varying,
    ADD COLUMN created_date character varying,
    ADD COLUMN last_modified_by character varying,
    ADD COLUMN last_modified_date character varying,
    ADD COLUMN 	deleted boolean;
