package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.Bank
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.DisbursementCheck
import com.hisd3.hismk2.domain.accounting.DisbursementExpense
import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.accounting.BankRepository
import com.hisd3.hismk2.rest.dto.DisbursementDto
import com.hisd3.hismk2.rest.dto.DisbursementExpDto
import com.hisd3.hismk2.services.GeneratorService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@GraphQLApi
class DisbursementExpenseServices extends AbstractDaoService<DisbursementExpense> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	DisbursementServices disbursementServices


    DisbursementExpenseServices() {
		super(DisbursementExpense.class)
	}
	
	@GraphQLQuery(name = "disExpById")
	DisbursementExpense disExpById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "disExpByParent", description = "Find DisbursementExpense by Parent")
	List<DisbursementExpense> disExpByParent(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select ds from DisbursementExpense ds where ds.disbursement.id = :id", ["id": id]).resultList
	}

	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertExp")
	DisbursementExpense upsertExp(
		 @GraphQLArgument(name="it") DisbursementExpDto it,
		 @GraphQLArgument(name="parent")	Disbursement parent
	) {
		DisbursementExpense upsert = new DisbursementExpense()
		def trans = objectMapper.convertValue(it.transType, ExpenseTransaction.class)
		def dep = objectMapper.convertValue(it.department, Department.class)
		if(!it.isNew){
			upsert = findOne(UUID.fromString(it.id))
		}
		upsert.disbursement = parent
		upsert.department = dep
		upsert.transType = trans
		upsert.amount = it.amount
		upsert.remarks = it.remarks
		
		save(upsert)
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "removeExpense")
	DisbursementExpense removeExpense(
			@GraphQLArgument(name = "id") UUID id
	) {
		def ex = findOne(id)
		//update parent
		disbursementServices.updateRemove(ex.disbursement.id, "EX", ex.amount)
		delete(ex)
		return ex
	}

}
