ALTER TABLE bms.rooms
ADD COLUMN IF NOT EXISTS is_ward bool default false;