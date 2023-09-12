package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.accounting.AccountsPayable
import com.hisd3.hismk2.domain.accounting.AccountsPayableDetails
import com.hisd3.hismk2.domain.accounting.DebitMemo
import com.hisd3.hismk2.domain.accounting.Disbursement
import com.hisd3.hismk2.domain.accounting.DisbursementAp
import com.hisd3.hismk2.domain.accounting.Reapplication
import com.hisd3.hismk2.domain.inventory.ReceivingReport
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.rest.dto.AccountPayableDetialsDto
import com.hisd3.hismk2.rest.dto.DisbursementApDto
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
class DisbursementApServices extends AbstractDaoService<DisbursementAp> {

	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	AccountsPayableServices accountsPayableServices

	@Autowired
	ReapplicationService reapplicationService

	@Autowired
	DebitMemoService debitMemoService

	@Autowired
	DisbursementServices disbursementServices


    DisbursementApServices() {
		super(DisbursementAp.class)
	}
	
	@GraphQLQuery(name = "apApplicationById")
	DisbursementAp apApplicationById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}
	
	@GraphQLQuery(name = "apAppByDis")
	List<DisbursementAp> apAppByDis(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select da from DisbursementAp da where da.disbursement.id = :id", ["id": id]).resultList
	}

	@GraphQLQuery(name = "apReapplication")
	List<DisbursementAp> apReapplication(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select da from DisbursementAp da where da.reapplication = :id", ["id": id]).resultList
	}

	@GraphQLQuery(name = "apDebitMemo")
	List<DisbursementAp> apDebitMemo(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select da from DisbursementAp da where da.debitMemo.id = :id", ["id": id]).resultList
	}

	@GraphQLQuery(name = "apByDis")
	List<DisbursementAp> apByDis(@GraphQLArgument(name = "id") UUID id) {
		createQuery("Select da from DisbursementAp da where da.disbursement.id = :id and da.reapplication is null", ["id": id]).resultList
	}

	//mutations
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDisAp")
	DisbursementAp upsertDisAp(
			@GraphQLArgument(name="it")DisbursementApDto it,
			@GraphQLArgument(name="parent") Disbursement parent
	) {
		DisbursementAp upsert = new DisbursementAp()
		def ap = objectMapper.convertValue(it.payable, AccountsPayable.class)
		if(!it.isNew){
			upsert = findOne(it.id)
		}
		upsert.payable = ap
		upsert.disbursement = parent
		upsert.appliedAmount = it.appliedAmount
		upsert.vatRate = it.vatRate
		upsert.vatInclusive = it.vatInclusive
		upsert.vatAmount = it.vatAmount
		upsert.ewtDesc = it.ewtDesc
		upsert.ewtRate = it.ewtRate
		upsert.ewtAmount = it.ewtAmount
		upsert.grossAmount = it.grossAmount
		upsert.discount = it.discount
		upsert.netAmount = it.netAmount
		save(upsert)
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDisReap")
	DisbursementAp upsertDisReap(
			@GraphQLArgument(name="it")	DisbursementApDto it,
			@GraphQLArgument(name="parent") Reapplication parent
	) {
		DisbursementAp upsert = new DisbursementAp()
		def ap = objectMapper.convertValue(it.payable, AccountsPayable.class)
		def dis = objectMapper.convertValue(parent.disbursement, Disbursement.class)
		if(!it.isNew){
			upsert = findOne(it.id)
		}
		upsert.payable = ap
		upsert.disbursement = dis
		upsert.reapplication = parent.id
		upsert.appliedAmount = it.appliedAmount
		upsert.vatRate = it.vatRate
		upsert.vatInclusive = it.vatInclusive
		upsert.vatAmount = it.vatAmount
		upsert.ewtDesc = it.ewtDesc
		upsert.ewtRate = it.ewtRate
		upsert.ewtAmount = it.ewtAmount
		upsert.grossAmount = it.grossAmount
		upsert.discount = it.discount
		upsert.netAmount = it.netAmount
		save(upsert)
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertDisDM")
	DisbursementAp upsertDisDM(
			@GraphQLArgument(name="it")	DisbursementApDto it,
			@GraphQLArgument(name="parent") DebitMemo parent
	) {
		DisbursementAp upsert = new DisbursementAp()
		def ap = objectMapper.convertValue(it.payable, AccountsPayable.class)
		if(!it.isNew){
			upsert = findOne(it.id)
		}
		upsert.payable = ap
		upsert.debitMemo = parent
		upsert.appliedAmount = it.appliedAmount
		upsert.vatRate = it.vatRate
		upsert.vatInclusive = it.vatInclusive
		upsert.vatAmount = it.vatAmount
		upsert.ewtDesc = it.ewtDesc
		upsert.ewtRate = it.ewtRate
		upsert.ewtAmount = it.ewtAmount
		upsert.grossAmount = it.grossAmount
		upsert.discount = it.discount
		upsert.netAmount = it.netAmount
		save(upsert)
	}


	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "removeApApp")
	DisbursementAp removeApApp(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "parent") UUID parent
	) {
		def details = findOne(id)
		//update AP
		accountsPayableServices.updateApForRemove(parent, details.disbursement.disNo, details.appliedAmount, details.posted)
		if(details.reapplication){
			reapplicationService.updateReappForRemove(details.reapplication, details.discount, details.ewtAmount,details.appliedAmount)
		}else{
			disbursementServices.updateRemoveAp(details.disbursement.id, details.discount, details.ewtAmount,details.appliedAmount)
		}
		delete(details)
		return details
	}

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "removeDMDetails")
	DisbursementAp removeDMDetails(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "parent") UUID parent,
			@GraphQLArgument(name = "type") String type
	) {
		def details = findOne(id)
		//update AP
		accountsPayableServices.updateApForRemoveDM(parent, details.debitMemo.debitNo, details.appliedAmount, details.posted,type)
		debitMemoService.updateDMforRemove(details.debitMemo.id, details.discount, details.ewtAmount, details.appliedAmount)
		delete(details)

		return details
	}

	@Transactional(rollbackFor = Exception.class)
	DisbursementAp updateDisApPosted(
			DisbursementAp disAP,
			Boolean status
	) {
		def details = disAP
		details.posted = status
		save(details)
	}
}
