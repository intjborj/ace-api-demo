ALTER TABLE "pms"."cases"
  ADD COLUMN "occupation" varchar(255),
  ADD COLUMN "company_name" varchar(255),
  ADD COLUMN "company_address" varchar(255),
  ADD COLUMN "company_contact" varchar(255),

  ADD COLUMN "emergency_contact_name" varchar(255),
  ADD COLUMN "emergency_contact_address" varchar(255),
  ADD COLUMN "emergency_contact_relation" varchar(255),
  ADD COLUMN "emergency_contact" varchar(255),

  ADD COLUMN "guarantor_name" varchar(255),
  ADD COLUMN "guarantor_address" varchar(255),
  ADD COLUMN "guarantor_relation" varchar(255),
  ADD COLUMN "guarantor_contact" varchar(255);

ALTER TABLE "pms"."patients"
  DROP COLUMN "emergency_contact_name",
  DROP COLUMN "emergency_contact_address",
  DROP COLUMN "emergency_contact_relationship",
  DROP COLUMN "emergency_contact_no";