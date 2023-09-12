package com.hisd3.hismk2.graphqlservices.inventoryv2

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.AccountsPayableDetails
import com.hisd3.hismk2.domain.inventory.BeginningDetails
import com.hisd3.hismk2.domain.inventory.BeginningTransaction
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.UserRepository
import com.hisd3.hismk2.repository.hrm.EmployeeRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import com.hisd3.hismk2.rest.dto.AccountPayableDetialsDto
import com.hisd3.hismk2.rest.dto.BeginningItemDto
import com.hisd3.hismk2.services.GeneratorService
import com.sun.org.apache.xpath.internal.operations.Bool
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
@Transactional(rollbackFor = Exception.class)
class ServiceBeginningDetails extends AbstractDaoService<BeginningDetails> {

	ServiceBeginningDetails() {
		super(BeginningDetails.class)
	}

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	GeneratorService generatorService

	@Autowired
	UserRepository userRepository

	@Autowired
	EmployeeRepository employeeRepository

	@Autowired
	ItemRepository itemRepository

	@Autowired
	DepartmentRepository departmentRepository

	@GraphQLQuery(name = "begDetailsById")
	List<BeginningDetails> begDetailsById(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "filter") String filter
	) {
		createQuery("Select b from BeginningDetails b where b.beginning.id = :id and lower(b.item.descLong) like lower(concat('%',:filter,'%'))",
				[filter: filter, id: id]).resultList.sort{it.item.descLong}
	}

	// Mutation
	@GraphQLMutation(name = "upsertBegDetails")
	BeginningDetails upsertBegDetails(
			@GraphQLArgument(name="it") BeginningItemDto it,
			@GraphQLArgument(name="bt") BeginningTransaction bt,
			@GraphQLArgument(name="item") UUID item,
			@GraphQLArgument(name="dep") UUID dep
	) {
		BeginningDetails upsert = new BeginningDetails()
		if(!it.isNew){
			upsert = findOne(UUID.fromString(it.id))
		}
		upsert.beginning = bt
		upsert.item = itemRepository.findById(item).get()
		upsert.department = departmentRepository.findById(dep).get()

		upsert.qty = it.qty
		upsert.unitCost = it.unitCost
		upsert.posted = it.posted

		save(upsert)
	}

	@GraphQLMutation(name = "updateStatusBegItem")
	BeginningDetails updateStatusBegItem(
			@GraphQLArgument(name="id") UUID id,
			@GraphQLArgument(name="status") Boolean status
	) {
		def upsert = findOne(id)
		upsert.posted = status
		save(upsert)
	}

	@GraphQLMutation(name = "deleteBeginningDetails")
	Boolean deleteBeginningDetails(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			BeginningDetails dependent = findOne(id)
			delete(dependent)
			return true
		}
		return false
	}

	@GraphQLQuery(name = "postItemsBeginning")
	List<BeginningDetails> postItemsBeginning(
			@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("Select b from BeginningDetails b where b.beginning.id = :id and b.qty > 0 and b.unitCost > 0",
				[id: id]).resultList.sort{it.item.descLong}
	}

	@GraphQLQuery(name = "getPostedBeginningItems")
	List<BeginningDetails> getPostedBeginningItems(
			@GraphQLArgument(name = "id") UUID id
	) {
		createQuery("Select b from BeginningDetails b where b.beginning.id = :id and b.posted = true",
				[id: id]).resultList.sort{it.item.descLong}
	}

	@GraphQLQuery(name = "totalSumPostBeginning")
	BigDecimal totalSumPostBeginning(
			@GraphQLArgument(name = "id") UUID id
	) {
		getSum("Select b from BeginningDetails b where b.beginning.id = :id and b.qty > 0 and b.unitCost > 0",
				[id: id])
	}

}
