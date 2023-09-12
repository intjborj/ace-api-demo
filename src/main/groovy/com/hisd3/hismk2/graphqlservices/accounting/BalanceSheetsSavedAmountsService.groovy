package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.BalanceSheetSavedAmounts
import com.hisd3.hismk2.domain.accounting.Fiscal
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
import org.springframework.stereotype.Service

import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

@Service
@GraphQLApi
class BalanceSheetsSavedAmountsService extends AbstractDaoService<BalanceSheetSavedAmounts> {

	@Autowired
	GeneratorService generatorService

    BalanceSheetsSavedAmountsService() {
		super(BalanceSheetSavedAmounts.class)
	}

	BalanceSheetSavedAmounts getBSheetsAmountsByYear(String year){
		List<BalanceSheetSavedAmounts> returned =  createQuery("Select d from BalanceSheetSavedAmounts d where d.year = :year ",
				[year: year]).resultList
		if(returned){
			return returned[0]
		}
		return  new BalanceSheetSavedAmounts()
	}
}
