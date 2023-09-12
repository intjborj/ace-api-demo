package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.domain.billing.Discount
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.services.GeneratorService
import com.hisd3.hismk2.services.GeneratorType
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class DiscountsService extends AbstractDaoService<Discount> {
	
	DiscountsService() {
		super(Discount.class)
	}
	
	@Autowired
	GeneratorService generatorService
	
	@GraphQLQuery(name = "discountById")
	Discount discountById(
			@GraphQLArgument(name = "id") UUID id
	) {
		return findOne(id)
	}
	
	@GraphQLQuery(name = "discounts")
	Page<Discount> getDiscounts(
			@GraphQLArgument(name = "filter") String filter,
			@GraphQLArgument(name = "page") Integer page,
			@GraphQLArgument(name = "size") Integer size
	) {
		
		getPageable(
				"""
              Select c from Discount c  where lower(c.discount) like lower(concat('%',:filter,'%')) and c.active=true  order by c.code desc
				""",
				"""
				 Select count(c) from Discount c  where lower(c.discount) like lower(concat('%',:filter,'%')) and c.active=true
				""",
				page,
				size,
				[filter: filter]
		)
		
	}
	
	@GraphQLMutation
	Discount upsertDiscounts(
			@GraphQLArgument(name = "id") UUID id,
			@GraphQLArgument(name = "fields") Map<String, Object> fields
	) {
		
		upsertFromMap(id, fields, { Discount entity, boolean forInsert ->
			if (forInsert) {
				entity.code = generatorService.getNextValue(GeneratorType.BILLING_DISCOUNT_ID, {
					return "DSC-" + StringUtils.leftPad(it.toString(), 6, "0")
				})
			}
		})
		
	}
}
