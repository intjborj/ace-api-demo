ALTER TABLE "pms"."cases"
    ADD COLUMN "pain_score" varchar,
    ADD COLUMN "fall_assessment" varchar;

ALTER TABLE "pms"."outputs"
    ADD COLUMN "blood_loss" varchar;