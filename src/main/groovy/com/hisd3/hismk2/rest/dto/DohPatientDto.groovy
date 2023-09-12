package com.hisd3.hismk2.rest.dto

class HospOptSummaryOfPatients {
	String hfhudcode
	Integer totalinpatients
	Integer totalnewborn
	Integer totaldischarges
	Integer totalpad
	Integer totalibd
	Integer totalinpatienttransto
	Integer totalinpatienttransfrom
	Integer totalpatientsremaining
	Integer reportingyear
}

class HospOptDischargesSpecialty {
	String hfhudcode
	Integer typeofservice
	Integer nopatients
	Integer totallengthstay
	Integer nppay
	Integer nphservicecharity
	Integer nphtotal
	Integer phpay
	Integer phservice
	Integer phtotal
	Integer hmo
	Integer owwa
	Integer recoveredimproved
	Integer transferred
	Integer hama
	Integer absconded
	Integer unimproved
	Integer deathsbelow48
	Integer deathsover48
	Integer totaldeaths
	Integer totaldischarges
	Integer remarks
	Integer reportingyear
}

class HospOptDischargesSpecialtyOthers {
	String hfhudcode
	Integer othertypeofservicespecify
	Integer nopatients
	Integer totallengthstay
	Integer nppay
	Integer nphservicecharity
	Integer nphtotal
	Integer phpay
	Integer phservice
	Integer phtotal
	Integer hmo
	Integer owwa
	Integer recoveredimproved
	Integer transferred
	Integer hama
	Integer absconded
	Integer unimproved
	Integer deathsbelow48
	Integer deathsover48
	Integer totaldeaths
	Integer totaldischarges
	Integer remarks
	Integer reportingyear
}
