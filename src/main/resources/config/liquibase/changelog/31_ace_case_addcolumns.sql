ALTER TABLE "pms"."cases"
    ADD COLUMN "history_input_datetime" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN "triage" varchar(255),
    ADD COLUMN "height" varchar(255),
    ADD COLUMN "weight" varchar(255),

    ADD COLUMN "initial_BP" varchar(255),
    ADD COLUMN "initial_temperature" varchar(255),
    ADD COLUMN "initial_pulse" varchar(255),
    ADD COLUMN "initial_resp" varchar(255),
    ADD COLUMN "initial_o2sat" varchar(255),

    ADD COLUMN "followup_datetime" timestamp(6) NULL DEFAULT CURRENT_TIMESTAMP,
    ADD COLUMN "disposition" varchar(255),
    ADD COLUMN "reason_for_transfer_out" text,
    ADD COLUMN "home_medication" text,
    ADD COLUMN "special_instructions" text,
    ADD COLUMN "lacerated_wound" text,
    ADD COLUMN "head_injury" text,
    ADD COLUMN "pertinent_past_medical_history" text,

    ADD COLUMN "transferred_in" boolean,
    ADD COLUMN "reason_for_transfer_in" text,
    ADD COLUMN "originating_HCI" varchar(255);