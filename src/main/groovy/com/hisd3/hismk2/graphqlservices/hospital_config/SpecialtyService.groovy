package com.hisd3.hismk2.graphqlservices.hospital_config

import com.hisd3.hismk2.domain.hospital_config.Specialty
import com.hisd3.hismk2.repository.hospital_config.SpecialtyRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class SpecialtyService {
	
	@Autowired
	SpecialtyRepository specialtyRepository
	
	@GraphQLQuery(name = "specialties", description = "get doctor specialties")
	List<Specialty> getSpecialties() {
		return specialtyRepository.findAll()
	}
	
	@GraphQLQuery(name = "activeSpecialties", description = "get doctor specialties")
	List<Specialty> availableSpecialties() {
		return specialtyRepository.availableSpecialties()
	}
}
