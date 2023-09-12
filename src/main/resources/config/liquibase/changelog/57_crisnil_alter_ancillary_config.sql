ALTER TABLE "ancillary"."ancillary_config"
  DROP COLUMN "setttings_key",
  DROP COLUMN "value",
  ADD COLUMN "department" uuid,
  ADD COLUMN "tcp" bool,
  ADD COLUMN "port" varchar,
  ADD COLUMN "ip_address" varchar,
  ADD COLUMN "smb_path" varchar,
  ADD COLUMN "demo" bool,
  ADD COLUMN "enabled" bool,
  ADD COLUMN "username" varchar,
  ADD COLUMN "password" varchar,
  ADD COLUMN "interface_direction" varchar;
