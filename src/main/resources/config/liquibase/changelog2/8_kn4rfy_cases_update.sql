alter table pms.cases
  add column room_in bool default false,
  add column additional_rooms text;
