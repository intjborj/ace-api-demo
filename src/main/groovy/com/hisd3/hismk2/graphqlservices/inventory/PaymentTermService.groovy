package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.PaymentTerm
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.PaymentTermRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class PaymentTermService {
	
	@Autowired
	PaymentTermRepository paymentTermRepository

	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "paymentTermList", description = "List of Payment Term")
	List<PaymentTerm> getPaymentTerm() {
		return paymentTermRepository.findAll().sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "paymentTermFilter", description = "List of Payment Term")
	List<PaymentTerm> paymentTermFilter(@GraphQLArgument(name = "filter") String filter) {
		return paymentTermRepository.paymentTermFilter(filter).sort { it.createdDate }
	}

	@GraphQLQuery(name = "paymentTermFilterPage", description = "List of Page Payment")
	Page<PaymentTerm> paymentTermFilterPage(@GraphQLArgument(name = "page") Integer page, // zero based
													@GraphQLArgument(name = "size") Integer pageSize,
													@GraphQLArgument(name = "filter") String filter) {

		return paymentTermRepository.paymentTermFilterPage(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "paymentCode"))
	}
	
	@GraphQLQuery(name = "paymentTermActive", description = "List of Active Payment Term")
	List<PaymentTerm> paymentTermActive() {
		return paymentTermRepository.paymentTermActive().sort { it.createdDate }
	}
	
	//validation
	@GraphQLQuery(name = "isPaymentTermCodeUnique", description = "Check if PaymentTermCode exists")
	Boolean findOneByPaymentTermCode(@GraphQLArgument(name = "paymentCode") String paymentCode) {
		return !paymentTermRepository.findOneByPaymentTermCode(paymentCode)
	}
	
	@GraphQLQuery(name = "isPaymentTermNameUnique", description = "Check if PaymentTermName exists")
	Boolean findOneByPaymentTermName(@GraphQLArgument(name = "paymentDesc") String paymentDesc) {
		return !paymentTermRepository.findOneByPaymentTermName(paymentDesc)
	}
	//end validation

	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertPaymentTerms", description = "upsert")
	GraphQLRetVal<Boolean> upsertPaymentTerms(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		PaymentTerm term = new PaymentTerm()
		def obj = objectMapper.convertValue(fields, PaymentTerm.class)
		def result = new GraphQLRetVal<Boolean>(true,true,"Payment Term Added")
		def checkCode = paymentTermRepository.findOneByPaymentTermCode(obj.paymentCode)
		def checkDesc = paymentTermRepository.findOneByPaymentTermName(obj.paymentDesc)
		if(id){
			term = paymentTermRepository.findById(id).get()
			term.paymentCode = obj.paymentCode
			term.paymentDesc = obj.paymentDesc
			term.paymentNoDays = obj.paymentNoDays
			term.isActive = obj.isActive
			paymentTermRepository.save(term)
			result = new GraphQLRetVal<Boolean>(true,true,"Payment Term Updated")
		}else{
			if(checkCode || checkDesc){
				result =  new GraphQLRetVal<Boolean>(false,false,"Payment Term Code or Description already exist")
			}else{
				term.paymentCode = obj.paymentCode
				term.paymentDesc = obj.paymentDesc
				term.paymentNoDays = obj.paymentNoDays
				term.isActive = obj.isActive
				paymentTermRepository.save(term)
			}
		}
		return result
	}
}
