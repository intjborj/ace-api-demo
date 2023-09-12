package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.DisbursementExpense
import com.hisd3.hismk2.domain.accounting.DisbursementWtx
import com.hisd3.hismk2.domain.accounting.ExpenseTransaction
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.accounting.BankRepository
import com.hisd3.hismk2.rest.dto.DisbursementExpDto
import com.hisd3.hismk2.rest.dto.DisbursementWtxDto
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
class DisbursementWtxServices extends AbstractDaoService<DisbursementWtx> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	DisbursementServices disbursementServices


    DisbursementWtxServices() {
		super(DisbursementWtx.class)
	}
	
	@GraphQLQuery(name = "disWtxById")
	DisbursementWtx disWtxById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "disWtxByParent", description = "Find DisbursementWtx by Parent")
	List<DisbursementWtx> disWtxByParent(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select ds from DisbursementWtx ds where ds.disbursement.id = :id", ["id": id]).resultList
	}

	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertWtx")
	DisbursementWtx upsertWtx(
			@GraphQLArgument(name="it")	DisbursementWtxDto it,
			@GraphQLArgument(name="parent") Disbursement parent
	) {
		DisbursementWtx upsert = new DisbursementWtx()
		if(!it.isNew){
			upsert = findOne(UUID.fromString(it.id))
		}
		upsert.disbursement = parent
		upsert.ewtDesc = it.ewtDesc
		upsert.ewtRate = it.ewtRate
		upsert.ewtAmount = it.ewtAmount
		
		save(upsert)
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "removeWtx")
	DisbursementWtx removeWtx(
			@GraphQLArgument(name = "id") UUID id
	) {
		def wtx = findOne(id)
		//update parent
		disbursementServices.updateRemove(wtx.disbursement.id, "WTX", wtx.ewtAmount)
		delete(wtx)
		return wtx
	}

}
