package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.domain.billing.CompanyAccount
import com.hisd3.hismk2.domain.billing.CompanyType
import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.billing.InvestorDependent
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.billing.InvestorDependentRepository
import com.hisd3.hismk2.repository.billing.InvestorsRepository
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
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class CompanyTypeService extends AbstractDaoService<CompanyType> {

	CompanyTypeService() {
		super(CompanyType.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService

	@Autowired
	GeneratorService generatorService

	@GraphQLQuery(name = "companyTypeById")
	CompanyType companyTypeById(
			@GraphQLArgument(name = "id") UUID id
	) {
		findOne(id)
	}

	@GraphQLQuery(name = "companyTypes")
	Page<CompanyType> companyTypes(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {

		getPageable("Select c from CompanyType c  where lower(c.description) like lower(concat('%',:filter,'%')) and c.active = true  order by c.code",
				"Select count(c) from CompanyType c  where lower(c.description) like lower(concat('%',:filter,'%')) and c.active = true ",
				page,
				size,
				[
						filter: filter
				]
		)
	}
	
	@GraphQLMutation
	CompanyType upsertCompanyType(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		
		if (id) {
			def item = findOne(id)
			entityObjectMapperService.updateFromMap(item, fields)
			
			save(item)
			
		} else {
			def item = new CompanyType()
			entityObjectMapperService.updateFromMap(item, fields)
			item.code = generatorService.getNextValue(GeneratorType.COMPANY_TYPE, {
				return StringUtils.leftPad(it.toString(), 6, "0")
			})
			save(item)
		}
	}

}
