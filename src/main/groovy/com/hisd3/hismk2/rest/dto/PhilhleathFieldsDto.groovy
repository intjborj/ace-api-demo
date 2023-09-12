package com.hisd3.hismk2.rest.dto

class PhilhleathFieldsDto {
	String hci_name,
	       hci_address,
	       patient_middle,
	       patient_first,
	       patient_last,
	       patient_name,
	       patient_age,
	       patient_gender,
	       patient_pin,
	       date_discharge,
	       time_discharge,
	       time_admitted,
	       date_admitted,
	       time_discharge_aa,
	       time_admitted_aa,
	       chief_complaints,
	       admitting_diag,
	       discharge_diag,
	       date_time_now,
	       history_presentillness
	
	Boolean dischargeDispositionDischarged,
	        dischargeDispositionDama,
	        dischargeDispositionAbsconded,
	        dischargeDispositionTransfered,
	        dischargeDispositionAutopsied
	
	Boolean dischargeConditionImproved,
	        dischargeConditionUnimproved,
	        dischargeConditionRecovered,
	        dischargeConditionExpired
	
}
