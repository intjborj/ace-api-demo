package com.hisd3.hismk2.graphqlservices

import com.hisd3.hismk2.domain.Authority
import com.hisd3.hismk2.domain.hospital_config.Constant
import com.hisd3.hismk2.domain.payroll.Timekeeping
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.AuthorityRepository
import com.hisd3.hismk2.repository.hospital_config.ConstantRepository                   
import groovy.transform.Canonical
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional


@Canonical
class InvestorConfig {
	Constant parValue
	Constant subscriptionRebate
	Constant subscriptionRebateType
}

enum InvestorConf {
	INVESTOR_REBATE_DISCOUNT,
	PAR_VALUE,
	INVESTOR_REBATE_DISCOUNT_VALUE_TYPE,
}


@TypeChecked
@Component
@GraphQLApi
class BackOfficeConfigService {
	@Autowired
	private ConstantRepository constantRepository
	
	@Autowired
	private AuthorityRepository authorityRepository
	
	//============== All Queries ====================
	
	@GraphQLQuery(name = "getDefaultParValue", description = "Get Default ParValue")
	InvestorConfig getDefaultParValue() {
		InvestorConfig investorConfig = new InvestorConfig()
		def configs = constantRepository.findByNames([InvestorConf.INVESTOR_REBATE_DISCOUNT.name(),InvestorConf.PAR_VALUE.name(),InvestorConf.INVESTOR_REBATE_DISCOUNT_VALUE_TYPE.name()])
		configs.each {
			if(it.name.equalsIgnoreCase(InvestorConf.PAR_VALUE.name()))
				investorConfig.parValue = it
			if(it.name.equalsIgnoreCase(InvestorConf.INVESTOR_REBATE_DISCOUNT.name()))
				investorConfig.subscriptionRebate = it
			if(it.name.equalsIgnoreCase(InvestorConf.INVESTOR_REBATE_DISCOUNT_VALUE_TYPE.name()))
				investorConfig.subscriptionRebateType = it
		}
		return  investorConfig
	}

	//============== All Mutations ====================

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation
	GraphQLRetVal<String> updateDefaultParValue(
			@GraphQLArgument(name = 'parValueAmount') String  parValueAmount,
			@GraphQLArgument(name = 'discountValueType') String  discountValueType,
			@GraphQLArgument(name = 'discountValue') String  discountValue
	) {
		if(parValueAmount) {
			def parValue = constantRepository.findById(UUID.fromString('f8e4700b-2e3d-4cb0-a628-eea14262b511')).get()
			parValue.value = parValueAmount
			constantRepository.save(parValue)
		}

		if(discountValueType){
			def discount = constantRepository.findByNames([InvestorConf.INVESTOR_REBATE_DISCOUNT_VALUE_TYPE.name()])
			if(discount.size() > 0) {
				Constant constant = discount[0]
				constant.value = discountValueType
				constantRepository.save(constant)
			}
			else{
				Constant constant = new Constant()
				constant.name = 'INVESTOR_REBATE_DISCOUNT_VALUE_TYPE'
				constant.value = discountValueType
				constant.shortCode = 'INVESTOR_REBATE_DISCOUNT_VALUE_TYPE'
				constantRepository.save(constant)
			}
		}

		if(discountValue){
			def discount = constantRepository.findByNames([InvestorConf.INVESTOR_REBATE_DISCOUNT.name()])
			if(discount.size() > 0) {
				Constant constant = discount[0]
				constant.value = discountValue
				constantRepository.save(constant)
			}
			else{
				Constant constant = new Constant()
				constant.name = 'INVESTOR_REBATE_DISCOUNT'
				constant.value = discountValue
				constant.shortCode = 'INVESTOR_REBATE_DISCOUNT'
				constantRepository.save(constant)
			}
		}


		return new GraphQLRetVal<String>("OK", true, "Successfully updated default par value")
	}


}
