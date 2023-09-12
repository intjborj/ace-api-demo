package com.hisd3.hismk2.graphqlservices.hospital_config

import com.hisd3.hismk2.domain.hospital_config.AdmissionConfiguration
import com.hisd3.hismk2.domain.hospital_config.HospitalInfo
import com.hisd3.hismk2.domain.hospital_config.LoginConfiguration
import com.hisd3.hismk2.domain.hospital_config.OperationalConfiguration
import com.hisd3.hismk2.domain.hospital_config.PharmacyConfiguration
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.hospital_config.AdmissionConfigurationRepository
import com.hisd3.hismk2.repository.hospital_config.HospitalInfoRepository
import com.hisd3.hismk2.repository.hospital_config.LoginConfigurationRepository
import com.hisd3.hismk2.repository.hospital_config.OperationalConfigurationRepository
import com.hisd3.hismk2.repository.hospital_config.PharmacyConfigurationRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
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
class HospitalConfigService {
	@Autowired
	HospitalInfoRepository hospitalInfoRepository
	
	@Autowired
	AdmissionConfigurationRepository admissionConfigurationRepository
	
	@Autowired
	OperationalConfigurationRepository operationalConfigurationRepository
	
	@Autowired
	PharmacyConfigurationRepository pharmacyConfigurationRepository

	@Autowired
    LoginConfigurationRepository loginConfigurationRepository

	@Autowired
	EntityObjectMapperService entityObjectMapperService
	
	@GraphQLQuery(name = "hospital_info", description = "get hospital information")
	HospitalInfo getHospitalInfo() {
		return hospitalInfoRepository.findAll().find()
	}

	@GraphQLQuery(name = "login_config", description = "get login configuration")
    LoginConfiguration getLoginConfig() {
		return loginConfigurationRepository.findAll().find()
	}
	
	@GraphQLQuery(name = "admission_config", description = "get admission config")
	AdmissionConfiguration getAdmissionConfig() {
		return admissionConfigurationRepository.findAll().find()
	}
	
	@GraphQLQuery(name = "pharmacy_config", description = "get pharmacy config")
	PharmacyConfiguration getPhamacyConfig() {
		return pharmacyConfigurationRepository.findAll().find()
	}
	
	@GraphQLQuery(name = "operational_config", description = "get operational config")
	OperationalConfiguration getOperationalConfig() {
		return operationalConfigurationRepository.findAll().find()
	}

	@GraphQLMutation(name="upsertHospitalConfig")
	GraphQLRetVal<HospitalInfo> upsertHospitalConfig(
			@GraphQLArgument(name="fields") Map<String,Object> fields,
			@GraphQLArgument(name="id") UUID id
	){
		try{
			def hospitalInfo
			if(id){
				HospitalInfo hi = hospitalInfoRepository.findById(id).get()
				entityObjectMapperService.updateFromMap(hi,fields)
				hospitalInfo = hospitalInfoRepository.save(hi)
			}else {
				HospitalInfo hi = new HospitalInfo()
				entityObjectMapperService.updateFromMap(hi,fields)
				hospitalInfo = hospitalInfoRepository.save(hi)
			}
			return new GraphQLRetVal<HospitalInfo>(hospitalInfo as HospitalInfo,true,'Success')
		}catch(e) {
			return  new GraphQLRetVal<HospitalInfo>(new HospitalInfo(),false,e.message)
		}

	}
}
