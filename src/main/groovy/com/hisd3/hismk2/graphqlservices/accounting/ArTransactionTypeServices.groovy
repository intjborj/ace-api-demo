package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountReceivable
import com.hisd3.hismk2.domain.accounting.ArTransaction
import com.hisd3.hismk2.domain.accounting.ArTransactionDetails
import com.hisd3.hismk2.domain.accounting.ArTransactionType
import com.hisd3.hismk2.domain.accounting.Integration
import com.hisd3.hismk2.domain.cashiering.PaymentTracker
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.cashiering.PaymentTrackerServices
import com.hisd3.hismk2.repository.accounting.*
import com.hisd3.hismk2.rest.dto.QueryErrorException
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
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
class ArTransactionTypeServices extends AbstractDaoService<ArTransactionType> {

	ArTransactionTypeServices() {
		super(ArTransactionType.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	ArTransactionTypeRepository arTransactionTypeRepository

	@Autowired
	IntegrationServices integrationServices

	@Autowired
	GeneratorService generatorService


	@Transactional(rollbackFor = QueryErrorException.class)
	@GraphQLMutation
	ArTransactionType upsertARTransactionType(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {

		//			SAVE AR TRANSACTION TYPE
		def item = new ArTransactionType()
		def newSave
		if(id){
			item = arTransactionTypeRepository.findById(id).get()
			entityObjectMapperService.updateFromMap(item,fields)
			newSave = arTransactionTypeRepository.save(item)
		}
		else {
			entityObjectMapperService.updateFromMap(item,fields)
			newSave = arTransactionTypeRepository.save(item)
		}

		return newSave

	}


	@GraphQLQuery(name = "getAllArTransactionType")
	List<ArTransactionType> getAllArTransactionType(
			@GraphQLArgument(name = "type") String type
	)
	{
		return  arTransactionTypeRepository.getARTransactionTypeByType()
	}

	@GraphQLQuery(name = "getAllArTransactionTypePageable")
	Page<ArTransactionType> getAllArTransactionTypePageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable(
				"""
              	Select c from ArTransactionType c  where lower(c.description) like lower(concat('%',:filter,'%')) order by c.createdDate
				""",
				"""
			 	Select count(c) from ArTransactionType c  where lower(c.description) like lower(concat('%',:filter,'%'))
				""",
				page,
				size,
				[
						filter: filter,
				]
		)

	}

	@GraphQLQuery(name = "getAllIntegrationPageable")
	Page<Integration> getAllIntegrationPageable(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		integrationServices.getPageable(
				"""
              	Select c from Integration c  where lower(c.description) like lower(concat('%',:filter,'%')) order by c.createdDate
				""",
				"""
			 	Select count(c) from Integration c  where lower(c.description) like lower(concat('%',:filter,'%'))
				""",
				page,
				size,
				[
						filter: filter,
				]
		)

	}

	@GraphQLQuery(name = "findArTransactionTypeById")
	ArTransactionType findArTransactionTypeById(@GraphQLArgument(name = "id") UUID id) {
		if(id){
			return  createQuery("""select b from ArTransactionType b where  b.id = :id""",
					[
							id: id,
					] as Map<String, Object>).setMaxResults(1).singleResult
		}
		return new ArTransactionType()
	}
}
