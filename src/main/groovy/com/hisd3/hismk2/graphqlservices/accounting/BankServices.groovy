package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.repository.accounting.BankRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
@GraphQLApi
class BankServices {
	
	@Autowired
	EntityObjectMapperService entityObjectMapperService
	
	@Autowired
	BankRepository bankRepository
	
	@Autowired
	GeneratorService generatorService
	
	@GraphQLQuery(name = "bankById")
	Bank bankById(
			@GraphQLArgument(name = "id") UUID id
	) {
		return bankRepository.findById(id).get()
	}

	@GraphQLQuery(name = "bankList")
	List<Bank> bankList() {
		return bankRepository.findAll()
	}
	
	@GraphQLQuery(name = "banks")
	Page<Bank> banks(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		return bankRepository.getBanks(filter, new PageRequest(page, size, Sort.Direction.ASC, "bankaccountId"))
		
	}
	
	@GraphQLMutation
	Bank upsertBanks(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		
		if (id) {
			def item = bankRepository.findById(id).get()
			entityObjectMapperService.updateFromMap(item, fields)
			
			bankRepository.save(item)
			
		} else {
			def item = new Bank()
			
			item.bankaccountId = generatorService.getNextValue(GeneratorType.BANKID, {
				return "BNK-" + StringUtils.leftPad(it.toString(), 6, "0")
			})
			
			entityObjectMapperService.updateFromMap(item, fields)
			bankRepository.save(item)
		}
	}
	
}
