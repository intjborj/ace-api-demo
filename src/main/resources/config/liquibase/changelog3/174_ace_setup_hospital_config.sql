DO $$
DECLARE
  myid uuid := uuid_generate_v4();
BEGIN
   IF (SELECT count(*) FROM hospital_configuration.operational_configuration) < 1 THEN
      INSERT INTO hospital_configuration.operational_configuration values (myid, false, 30000.00);
   END IF;
END $$;