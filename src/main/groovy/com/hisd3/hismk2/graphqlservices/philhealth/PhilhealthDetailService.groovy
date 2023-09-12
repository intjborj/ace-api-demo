package com.hisd3.hismk2.graphqlservices.philhealth

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.pms.PatientPhilhealthData
import com.hisd3.hismk2.repository.pms.PatientPhilhealthDataRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@TypeChecked
@Component
@GraphQLApi
class PhilhealthDetailService {
	
	@Autowired
	ObjectMapper objectMapper
	
	@Autowired
	PatientPhilhealthDataRepository patientPhilhealthDataRepository
	
	@GraphQLQuery(name = "philhealth_details")
	List<PatientPhilhealthData> getPhilhealthDetails(@GraphQLArgument(name = 'caseId') UUID caseId) {
		return patientPhilhealthDataRepository.findByCaseId(caseId).sort { a,b -> b.lastModifiedDate <=> a.lastModifiedDate}
	}
	
	@GraphQLMutation(name = "save_philheath_details")
	PatientPhilhealthData savePhilhealthDetails(@GraphQLArgument(name = "fields") Map<String, Object> fields) {
		
		PatientPhilhealthData toSave = objectMapper.convertValue(fields, PatientPhilhealthData)
		patientPhilhealthDataRepository.save(toSave)

		// As long as the entity has field id. it will automatically update the record. no need for the code below
		//        def upsert = new PatientPhilhealthData();
		//
		//        if(toSave.id){ // update
		//            upsert = patientPhilhealthDataRepository.findById(toSave.id).get()
		//            upsert.memberDob = toSave.memberDob
		//            upsert.memberPin = toSave.memberPin
		//            upsert.memberType = toSave.memberType
		//            upsert.memberGender = toSave.memberGender
		//            upsert.memberSuffix = toSave.memberSuffix
		//            upsert.memberAddress = toSave.memberAddress
		//            upsert.memberZipCode = toSave.memberZipCode
		//            upsert.memberRelation = toSave.memberRelation
		//            upsert.memberLastName = toSave.memberLastName
		//            upsert.memberFirstName = toSave.memberFirstName
		//            upsert.memberMiddleName = toSave.memberMiddleName
		//            upsert.memberCivilStatus = toSave.memberCivilStatus
		//            upsert.memberStateProvince = toSave.memberStateProvince
		//            upsert.memberCityMunicipality = toSave.memberCityMunicipality
		//            upsert.memberBarangay = toSave.memberBarangay
		//            upsert.memberCountry = toSave.memberCountry
		//
		//            patientPhilhealthDataRepository.save(toSave)
		//        }else{ //insert
		//            upsert.parentCase = toSave.parentCase
		//            upsert.memberDob = toSave.memberDob
		//            upsert.memberPin = toSave.memberPin
		//            upsert.memberType = toSave.memberType
		//            upsert.memberGender = toSave.memberGender
		//            upsert.memberSuffix = toSave.memberSuffix
		//            upsert.memberAddress = toSave.memberAddress
		//            upsert.memberZipCode = toSave.memberZipCode
		//            upsert.memberRelation = toSave.memberRelation
		//            upsert.memberLastName = toSave.memberLastName
		//            upsert.memberFirstName = toSave.memberFirstName
		//            upsert.memberMiddleName = toSave.memberMiddleName
		//            upsert.memberCivilStatus = toSave.memberCivilStatus
		//            upsert.memberStateProvince = toSave.memberStateProvince
		//            upsert.memberCityMunicipality = toSave.memberCityMunicipality
		//            upsert.memberBarangay = toSave.memberBarangay
		//            upsert.memberCountry = toSave.memberCountry
		//
		//            patientPhilhealthDataRepository.save(toSave)
		//        }
		//
		//        return upsert
		
	}
}
