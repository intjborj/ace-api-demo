package com.hisd3.hismk2.graphqlservices.doh

import com.hisd3.hismk2.dao.doh.DohPatientDao
import com.hisd3.hismk2.rest.dto.HospOptDischargesSpecialty
import com.hisd3.hismk2.rest.dto.HospOptDischargesSpecialtyOthers
import com.hisd3.hismk2.rest.dto.HospOptSummaryOfPatients
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import java.time.Instant

@TypeChecked
@Component
@GraphQLApi
class DohPatientService {
	
	@Autowired
	DohPatientDao dohPatientDao
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "hospOptSummaryOfPatients", description = "This function allow users to push II. Hospital Operations: Summary of Patients in the Hospital.")
	HospOptSummaryOfPatients hospOptSummaryOfPatients(@GraphQLArgument(name = "year") Instant year) {
		return dohPatientDao.hospOptSummaryOfPatients(year)
	}
	
	@GraphQLQuery(name = "hospOptDischargesSpecialty", description = "This function allow users to push II. Hospital Operations: B. Discharges - Type of Service and Total Discharges According to Specialty")
	List<HospOptDischargesSpecialty> hospOptDischargesSpecialty(@GraphQLArgument(name = "year") Instant year) {
		return dohPatientDao.hospOptDischargesSpecialty(year)
	}
	
	@GraphQLQuery(name = "hospOptDischargesSpecialtyOthers", description = "This function allow users to push II. Hospital Operations: B. Discharges - Type of Service and Total Discharges According to Specialty")
	List<HospOptDischargesSpecialtyOthers> hospOptDischargesSpecialtyOthers(@GraphQLArgument(name = "year") Instant year) {
		return dohPatientDao.hospOptDischargesSpecialtyOthers(year)
	}
}
