package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import com.hisd3.hismk2.domain.accounting.PettyCash
import com.hisd3.hismk2.domain.accounting.PettyCashItem
import com.hisd3.hismk2.domain.accounting.PettyCashOther
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.rest.dto.PCVItemsDto
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
class PettyCashOtherServices extends AbstractDaoService<PettyCashOther> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	DepartmentRepository departmentRepository


    PettyCashOtherServices() {
		super(PettyCashOther.class)
	}
	
	@GraphQLQuery(name = "othersById")
	PettyCashOther othersById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "othersByPetty")
	List<PettyCashOther> othersByPetty(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select da from PettyCashOther da where da.pettyCash.id = :id", ["id": id]).resultList
	}


	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertOthers")
	PettyCashOther upsertOthers(
			@GraphQLArgument(name="it") PCVOthersDto it,
			@GraphQLArgument(name="parent") PettyCash parent
	) {
		PettyCashOther upsert = new PettyCashOther()
		def type = objectMapper.convertValue(it.transType, ExpenseTransaction.class)
		def dep = objectMapper.convertValue(it.department, Department.class)
		if(!it.isNew){
			upsert = findOne(UUID.fromString(it.id))
		}
		upsert.transType = type
		upsert.pettyCash = parent
		upsert.department = dep
		upsert.amount = it.amount
		upsert.remarks = it.remarks

		save(upsert)
	}

}
