package com.hisd3.hismk2.graphqlservices.inventory

import com.fasterxml.jackson.databind.ObjectMapper
import com.hisd3.hismk2.domain.Department
import com.hisd3.hismk2.domain.inventory.AccountingCategory
import com.hisd3.hismk2.domain.inventory.Generic
import com.hisd3.hismk2.domain.inventory.Inventory
import com.hisd3.hismk2.domain.inventory.Item
import com.hisd3.hismk2.domain.inventory.ItemCategory
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.repository.DepartmentRepository
import com.hisd3.hismk2.repository.inventory.AccountingCategoryRepository
import com.hisd3.hismk2.repository.inventory.ItemCategoryRepository
import com.hisd3.hismk2.repository.inventory.ItemRepository
import groovy.transform.TypeChecked
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLContext
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
class AccountingCategoryService {

	@Autowired
	AccountingCategoryRepository accountingCategoryRepository

	@Autowired
	ObjectMapper objectMapper

	@Autowired
	DepartmentRepository departmentRepository
	
	@GraphQLQuery(name = "accountingCategoryList", description = "List of Accounting Category")
	List<AccountingCategory> accountingCategoryList() {
		return accountingCategoryRepository.findAll()
	}

	@GraphQLQuery(name = "activeAccountingCategories", description = "List of Active Accounting Category")
	List<AccountingCategory> activeAccountingCategories() {
		return accountingCategoryRepository.activeAccountingCategories()
	}

	@GraphQLQuery(name = "filterAccountingCategories", description = "List of Active Accounting Category")
	List<AccountingCategory> filterAccountingCategories(
			@GraphQLArgument(name = "filter") String filter
	) {
		return accountingCategoryRepository.filterAccountingCategories(filter)
	}

	@GraphQLQuery(name = "accountingCategoryPage", description = "List of Page Generics")
	Page<AccountingCategory> accountingCategoryPage(@GraphQLArgument(name = "page") Integer page, // zero based
									@GraphQLArgument(name = "size") Integer pageSize,
									@GraphQLArgument(name = "filter") String filter) {

		return accountingCategoryRepository.accountingCategoryPage(filter, new PageRequest(page, pageSize, Sort.Direction.ASC, "categoryCode"))
	}

	//contex
	@GraphQLQuery(name = "selectedDeps")
	List<Department> getSelectedDeps(@GraphQLContext AccountingCategory accountingCategory) {
		if(accountingCategory.departments){
			def list = accountingCategory.departments.split(',').collect{UUID.fromString(it)}
			return departmentRepository.getDepartmentWithIds(list)
		}else {
			return []
		}
	}

	//validation
	@GraphQLQuery(name = "isAcctCategoryCodeUnique", description = "Check if Category Code exists")
	Boolean findOneByAcctCategoryCode(@GraphQLArgument(name = "code") String code) {
		return !accountingCategoryRepository.findOneByAcctCategoryCode(code)
	}

	@GraphQLQuery(name = "isAcctCategoryNameUnique", description = "Check if Category Name exists")
	Boolean findOneByAcctCategoryName(@GraphQLArgument(name = "description") String description) {
		return !accountingCategoryRepository.findOneByAcctCategoryName(description)
	}

	//mutation
	@Transactional(rollbackFor = Exception.class)
	@GraphQLMutation(name = "upsertAcctCategory", description = "upsert")
	GraphQLRetVal<Boolean> upsertAcctCategory(
			@GraphQLArgument(name = "fields") Map<String, Object> fields,
			@GraphQLArgument(name = "id") UUID id
	) {
		AccountingCategory act = new AccountingCategory()
		def obj = objectMapper.convertValue(fields, AccountingCategory.class)
		def result = new GraphQLRetVal<Boolean>(true,true,"Accounting Category Added")
		def checkCode = accountingCategoryRepository.findOneByAcctCategoryCode(obj.categoryCode)
		def checkDesc = accountingCategoryRepository.findOneByAcctCategoryName(obj.categoryDescription)
		if(id){
			act = accountingCategoryRepository.findById(id).get()
			act.categoryCode = obj.categoryCode
			act.categoryDescription = obj.categoryDescription
			act.departments = obj.departments
			act.isActive = obj.isActive
			act.includeDepartments = obj.includeDepartments
			act.motherAccounts = obj.motherAccounts
			act.accountType = obj.accountType
			act.sourceColumn = obj.sourceColumn
			accountingCategoryRepository.save(act)
			result = new GraphQLRetVal<Boolean>(true,true,"Accounting Category Updated")
		}else{
			if(checkCode || checkDesc){
				result =  new GraphQLRetVal<Boolean>(false,false,"Accounting Category Code or Description already exist")
			}else{
				act.categoryCode = obj.categoryCode
				act.categoryDescription = obj.categoryDescription
				act.isActive = obj.isActive
				act.departments = obj.departments
				act.includeDepartments = obj.includeDepartments
				act.motherAccounts = obj.motherAccounts
				act.accountType = obj.accountType
				act.sourceColumn = obj.sourceColumn
				accountingCategoryRepository.save(act)
			}
		}
		return result
	}

}
