alter table pms.cases
	add rcid text;

alter table pms.cases drop column final_icd_code;
