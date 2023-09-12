ALTER TABLE appointment.appointment
ADD COLUMN purpose_of_testing varchar default null,
ADD COLUMN reason_of_testing varchar default null,
ADD COLUMN date_validity timestamp default null,
ADD COLUMN transportation varchar default null,
ADD COLUMN airline_sea_vessel varchar default null,
ADD COLUMN flight_vessel_no varchar default null,
ADD COLUMN country_destination varchar default null,
ADD COLUMN positive_covid_before varchar default null,
ADD COLUMN informant varchar default null,
ADD COLUMN realtion_informant varchar default null,
ADD COLUMN informant_contact varchar default null,
ADD COLUMN number_of_Test varchar default null,
ADD COLUMN covid_updates varchar default null,
ADD COLUMN outcome_condition varchar default null,
ADD COLUMN dod date default null,
ADD COLUMN immediate_cause varchar default null,
ADD COLUMN antecedent_cause varchar default null,
ADD COLUMN underlying_cause varchar default null,
ADD COLUMN contributory_conditions varchar default null,
ADD COLUMN dor date default null;


ALTER TABLE appointment.patients DROP COLUMN purpose_of_testing;
ALTER TABLE appointment.patients DROP COLUMN reason_of_testing;
ALTER TABLE appointment.patients DROP COLUMN date_validity;
ALTER TABLE appointment.patients DROP COLUMN transportation;
ALTER TABLE appointment.patients DROP COLUMN airline_sea_vessel;
ALTER TABLE appointment.patients DROP COLUMN flight_vessel_no;
ALTER TABLE appointment.patients DROP COLUMN country_destination;
ALTER TABLE appointment.patients DROP COLUMN positive_covid_before;
ALTER TABLE appointment.patients DROP COLUMN informant;
ALTER TABLE appointment.patients DROP COLUMN realtion_informant;
ALTER TABLE appointment.patients DROP COLUMN informant_contact;
ALTER TABLE appointment.patients DROP COLUMN number_of_test;
ALTER TABLE appointment.patients DROP COLUMN covid_updates;
ALTER TABLE appointment.patients DROP COLUMN outcome_condition;
ALTER TABLE appointment.patients DROP COLUMN dod;
ALTER TABLE appointment.patients DROP COLUMN immediate_cause;
ALTER TABLE appointment.patients DROP COLUMN antecedent_cause;
ALTER TABLE appointment.patients DROP COLUMN underlying_cause;
ALTER TABLE appointment.patients DROP COLUMN contributory_conditions;
ALTER TABLE appointment.patients DROP COLUMN dor;

