ALTER TABLE inventory.material_production
    ADD COLUMN date_trans date,
    ADD COLUMN is_posted bool default false;