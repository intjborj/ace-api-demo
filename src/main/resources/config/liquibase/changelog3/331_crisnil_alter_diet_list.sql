alter table dietary.patient_diet_list
	add employee_id uuid;

alter table dietary.patient_diet_list
	add alias varchar(50);

alter table dietary.patient_diet_list
	add meal_to_companion boolean default false;

