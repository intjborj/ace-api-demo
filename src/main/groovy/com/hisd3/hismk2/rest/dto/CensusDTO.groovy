package com.hisd3.hismk2.rest.dto

import com.hisd3.hismk2.domain.pms.Case

class CensusDTO {
	int inPatientsCount
	int outPatientsCount
	int erPatientsCount
}

class MyCensusDTO {
	List<Case> caseList
	Integer caseListCount
}

class DashboardCensusDto {
	int new_admission = 0
	int newborn = 0
	int discharge_alive = 0
	int discharge_expired = 0
	int discharge_ama = 0
	int transferred_to_this_facility = 0
	int transferred_to_facility = 0
	int absconded = 0
	int old_admissions = 0

	//NewAdmissionCensusDto newAdmissionCensusDto = new DashboardCensusDto()
	//DischargesCensusDto dischargesCensusDto = new DischargesCensusDto()
	//FloorCensusDto floorCensusDto = new FloorCensusDto()
	//FloorServicesCensusDto floorServicesCensusDto = new FloorServicesCensusDto()
	//RoDCensusDto roDCensusDto = new RoDCensusDto()
	//EROPDCensusDto eropdCensusDto = new EROPDCensusDto()
	//CumulativeMonthlyAdmissionsCensus cumulativeMonthlyAdmissionsCensus = new CumulativeMonthlyAdmissionsCensus()

	String admitting_officer  = "N/A"
}

class NewAdmissionCensusDto {
	int im = 0
	int pedia = 0
	int ob = 0
	int gyne = 0
	int surgery = 0
	int newborn = 0
	int icu_adult = 0
	int icu_pedia = 0
	int nicu = 0
}

class DischargesCensusDto {
	int alive = 0
	int expired = 0
	int discharge_ama = 0
	int transferred = 0

	int im = 0
	int pedia = 0
	int ob = 0
	int gyne = 0
	int surgery = 0
	int newborn = 0
	int icu_adult = 0
	int icu_pedia = 0
	int nicu = 0
}

class FloorCensusDto {
	Map<String, Integer> floors = new HashMap<>()
	Map<String, Integer> room_types = new HashMap<>()
}

class FloorServicesCensusDto {
	int im = 0
	int pedia = 0
	int ob = 0
	int gyne = 0
	int surgery = 0
	int ophtha = 0
	int ent = 0
	int derma = 0
	int onco = 0
	int rehab = 0

	int phic_member = 0
	int phic_to_follow = 0
	int nphic_member = 0
	int hmo = 0
	int new_patient = 0
	int readmitted = 0
}

class RoDCensusDto {
	Map<String, Integer> rod = new HashMap<>()
}

class EROPDCensusDto {
	int im = 0
	int pedia = 0
	int ob = 0
	int gyne = 0
	int surgery = 0
}


class CumulativeMonthlyAdmissionsCensus {
	Map<String, Integer> months = new HashMap<>()
}



