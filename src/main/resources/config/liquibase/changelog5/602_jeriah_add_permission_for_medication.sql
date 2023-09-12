    INSERT INTO t_permission (name, description)
    SELECT 'allow_to_discontinue_medication_mar', 'Permission to Discontinue Medication(MAR)'
    WHERE NOT EXISTS(
        SELECT 1 FROM public.t_permission WHERE name = 'allow_to_discontinue_medication_mar'
    );


  INSERT INTO t_permission (name, description)
  SELECT 'allow_to_shift_medication', 'Permission to Shift Medication'
  WHERE NOT EXISTS(
      SELECT 1 FROM public.t_permission WHERE name = 'allow_to_shift_medication'
    );

 INSERT INTO t_permission (name, description)
  SELECT 'allow_to_hold_medication', 'Permission to Hold Medication'
  WHERE NOT EXISTS(
      SELECT 1 FROM public.t_permission WHERE name = 'allow_to_hold_medication'
    );

INSERT INTO t_permission (name, description)
  SELECT 'allow_to_resume_medication', 'Permission to Resume Medication'
  WHERE NOT EXISTS(
      SELECT 1 FROM public.t_permission WHERE name = 'allow_to_resume_medication'
    );