package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.DebitMemo
import com.hisd3.hismk2.domain.accounting.DebitMemoDetails
import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import com.hisd3.hismk2.domain.accounting.PettyCash
import com.hisd3.hismk2.domain.accounting.PettyCashOther
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.rest.dto.DmDetailsDto
import com.hisd3.hismk2.rest.dto.PCVOthersDto
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class DebitMemoDetailsServices extends AbstractDaoService<DebitMemoDetails> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	DepartmentRepository departmentRepository


    DebitMemoDetailsServices() {
		super(DebitMemoDetails.class)
	}
	
	@GraphQLQuery(name = "dmDetailsById")
	DebitMemoDetails dmDetailsById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "dmDetials")
	List<DebitMemoDetails> dmDetials(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select dm from DebitMemoDetails dm where dm.debitMemo.id = :id", ["id": id]).resultList
	}


	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDmDetials")
	DebitMemoDetails upsertDmDetials(
			@GraphQLArgument(name="it") DmDetailsDto it,
			@GraphQLArgument(name="parent") DebitMemo parent
	) {
		DebitMemoDetails upsert = new DebitMemoDetails()
		def type = objectMapper.convertValue(it.transType, ExpenseTransaction.class)
		def dep = objectMapper.convertValue(it.department, Department.class)
		if(!it.isNew){
			upsert = findOne(UUID.fromString(it.id))
		}
		upsert.transType = type
		upsert.debitMemo = parent
		upsert.department = dep
		upsert.type = it.type
		upsert.percent = it.percent
		upsert.amount = it.amount
		upsert.remarks = it.remarks

		save(upsert)
	}

}
