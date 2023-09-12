package com.hisd3.hismk2.graphqlservices.billing

import com.hisd3.hismk2.domain.accounting.AccountsPayableDetails
import com.hisd3.hismk2.domain.billing.Investor
import com.hisd3.hismk2.domain.billing.InvestorDependent
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.repository.billing.InvestorDependentRepository
import com.hisd3.hismk2.repository.billing.InvestorsRepository
import com.hisd3.hismk2.services.EntityObjectMapperService
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.stereotype.Component

@Component
@GraphQLApi
class InvestorDependentService extends AbstractDaoService<InvestorDependent> {

	InvestorDependentService() {
		super(InvestorDependent.class)
	}

	@Autowired
	EntityObjectMapperService entityObjectMapperService
	
	@Autowired
	InvestorDependentRepository investorDependentRepository

	@GraphQLMutation
	Boolean deleteDependents(
			@GraphQLArgument(name = "id") UUID id
	) {
		if(id){
			InvestorDependent dependent = investorDependentRepository.findById(id).get()
			delete(dependent)
			return true
		}
		return false
	}
	
}
