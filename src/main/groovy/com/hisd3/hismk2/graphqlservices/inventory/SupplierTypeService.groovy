package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.inventory.PaymentTerm
import com.hisd3.hismk2.domain.inventory.SupplierType
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.inventory.SupplierTypeRepository
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@GraphQLApi
@TypeChecked
class SupplierTypeService {
	
	@Autowired
	SupplierTypeRepository supplierTypeRepository
	
	@Autowired
	GeneratorService generatorService

	@Autowired
	ObjectMapper objectMapper
	
	@GraphQLQuery(name = "supplierTypeList", description = "List of Supplier Types")
	List<SupplierType> getSupplierTypes() {
		return supplierTypeRepository.findAll().sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "supplierTypeFilter", description = "List of Supplier Types")
	List<SupplierType> supplierTypeFilter(@GraphQLArgument(name = "filter") String filter) {
		return supplierTypeRepository.supplierTypeFilter(filter).sort { it.createdDate }
	}
	
	@GraphQLQuery(name = "supplierTypeActive", description = "List of Active Supplier Types")
	List<SupplierType> supplierTypeActive() {
		return supplierTypeRepository.supplierTypeActive().sort { it.createdDate }
	}

	@GraphQLQuery(name = "supplierTypeActiveFilter", description = "List of Active Supplier Types")
	List<SupplierType> supplierTypeActiveFilter(@GraphQLArgument(name = "filter") String filter) {
		return supplierTypeRepository.supplierTypeActiveFilter(filter).sort { it.createdDate }
	}

	@GraphQLQuery(name = "supplierTypeFilterPage", description = "List of Page Supplier Type")
	Page<SupplierType> supplierTypeFilterPage(@GraphQLArgument(name = "page") Integer page, // zero based
											@GraphQLArgument(name = "size") Integer pageSize,
											@GraphQLArgument(name = "filter") String filter) {

		return supplierTypeRepository.supplierTypeFilterPage(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "supplierTypeCode"))
	}
	
	//validation
	@GraphQLQuery(name = "isSupplierTypeUnique", description = "Check if Supplier Type Code exists")
	Boolean findOneBySupplierTypeCode(@GraphQLArgument(name = "supplierTypeCode") String supplierTypeCode) {
		return !supplierTypeRepository.findOneBySupplierTypeCode(supplierTypeCode)
	}
	
	@GraphQLQuery(name = "isSupplierTypeNameUnique", description = "Check if Supplier Type Name exists")
	Boolean findOneBySupplierTypeName(@GraphQLArgument(name = "supplierTypeDesc") String supplierTypeDesc) {
		return !supplierTypeRepository.findOneBySupplierTypeName(supplierTypeDesc)
	}
	
	@GraphQLQuery(name = "getNextSupplierSubAccountCode", description = "Check if getNextSupplierSubAccountCode Type Name exists")
	String getNextSupplierSubAccountCode() {
		Long i = (Long) generatorService.getCurrentValue(GeneratorType.SUPPLIER_SUB_ACCOUNT_CODE).plus(1)
		return "VAS" + StringUtils.leftPad(i.toString(), 6, "0")
	}
	
	//end validation

	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertSupplierTypes", description = "upsert")
	GraphQLRetVal<Boolean> upsertSupplierTypes(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		SupplierType term = new SupplierType()
		def obj = objectMapper.convertValue(fields, SupplierType.class)
		def result = new GraphQLRetVal<Boolean>(true,true,"Supplier Types Added")
		def checkCode = supplierTypeRepository.findOneBySupplierTypeCode(obj.supplierTypeCode)
		def checkDesc = supplierTypeRepository.findOneBySupplierTypeName(obj.supplierTypeDesc)
		if(id){
			term = supplierTypeRepository.findById(id).get()
			term.supplierTypeCode = obj.supplierTypeCode
			term.supplierTypeDesc = obj.supplierTypeDesc
			term.isActive = obj.isActive
			supplierTypeRepository.save(term)
			result = new GraphQLRetVal<Boolean>(true,true,"Supplier Types Updated")
		}else{
			if(checkCode || checkDesc){
				result =  new GraphQLRetVal<Boolean>(false,false,"Supplier Types Code or Description already exist")
			}else{
				term.supplierTypeCode = obj.supplierTypeCode
				term.supplierTypeDesc = obj.supplierTypeDesc
				term.subAccountCode = null;
				term.supEwtRate = 0;
				term.isActive = obj.isActive
				supplierTypeRepository.save(term)
			}
		}
		return result
	}
}
