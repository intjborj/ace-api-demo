package com.hisd3.hismk2.graphqlservices.clinic

import com.hisd3.hismk2.domain.clinic.Clinic
import com.hisd3.hismk2.repository.clinic.ClinicRepository
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class ClinicService {
	
	@Autowired
	ClinicRepository clinicRepository
	
	@GraphQLQuery(name = "findAllClinics", description = "Find all clinics")
	List<Clinic> findAllClinics() {
		return clinicRepository.findAllClinics()
	}
}
