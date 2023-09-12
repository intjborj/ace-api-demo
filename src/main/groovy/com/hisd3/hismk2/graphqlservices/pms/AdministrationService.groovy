package com.hisd3.hismk2.graphqlservices.pms

import com.hisd3.hismk2.domain.pms.Administration
import com.hisd3.hismk2.domain.pms.PatientOwnMedicineAdministration
import com.hisd3.hismk2.repository.pms.AdministrationRepository
import com.hisd3.hismk2.repository.pms.PatientOwnMedicationAdministrationRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class AdministrationService {
	
	@Autowired
	private AdministrationRepository administrationRepository
	
	@Autowired
	private PatientOwnMedicationAdministrationRepository patientOwnMedicationAdministrationRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "administrations", description = "Get All Administration")
	List<Administration> findAll() {
		return administrationRepository.findAll().sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "administration", description = "Get Administration By Id")
	Administration findById(@GraphQLArgument(name = "id") UUID id) {
		return administrationRepository.findById(id).get()
	}
	
	@GraphQLQuery(name = "medicationAdministrations", description = "Get all Administrations by Medication Id")
	List<Administration> getMedicationAdministrations(@GraphQLArgument(name = "medication") UUID medication) {
		return administrationRepository.getMedicationAdministrations(medication).sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "patientOwnMedicationAdministrations", description = "Get all patientOwnMedicationAdministrations by Medication Id")
	List<PatientOwnMedicineAdministration> getPatientOwnMedicationAdministrations(@GraphQLArgument(name = "medication") UUID medication) {
		return patientOwnMedicationAdministrationRepository.getMedicationAdministrations(medication).sort {
			it.entryDateTime
		}
		//return administrationRepository.getMedicationAdministrations(medication).sort { it.entryDateTime }
	}
	
	@GraphQLQuery(name = "medicationAdministrationsByCase", description = "Get all Administrations by Case Id")
	List<Administration> getMedicationAdministrationsByCase(@GraphQLArgument(name = "caseId") UUID caseId) {
		return administrationRepository.getMedicationAdministrationsByCase(caseId).sort { it.entryDateTime }
	}
}
