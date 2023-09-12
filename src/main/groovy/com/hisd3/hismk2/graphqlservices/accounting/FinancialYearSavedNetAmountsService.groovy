package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.FinancialYearSavedNetAmounts
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
import java.time.temporal.TemporalAdjusters

@Service
@GraphQLApi
class FinancialYearSavedNetAmountsService extends AbstractDaoService<FinancialYearSavedNetAmounts> {

	@Autowired
	GeneratorService generatorService

	FinancialYearSavedNetAmountsService() {
		super(FinancialYearSavedNetAmounts.class)
	}

	FinancialYearSavedNetAmounts getBSheetsAmountsByYear(String year){
		List<FinancialYearSavedNetAmounts> returned =  createQuery("Select d from FinancialYearSavedNetAmounts d where d.year = :year ",
				[year: year]).resultList
		if(returned){
			return returned[0]
		}
		return  new FinancialYearSavedNetAmounts()
	}

	FinancialYearSavedNetAmounts onProcessedISPerYear(
			Integer year
	){
		LocalDate localStart = LocalDate.now().withYear(year).withMonth(1).with(TemporalAdjusters.firstDayOfMonth())
		LocalDate localEnd = LocalDate.now().withYear(year).withMonth(12).with(TemporalAdjusters.lastDayOfMonth())

		def bsSavedAmt = FinancialYearSavedNetAmounts.getBSheetsAmountsByYear(localStart.getYear().toString())
		if(bsSavedAmt.id){
			return  bsSavedAmt
		}
		else {
			List<IncomeStatementDto> incomeStatementDtoList = getIncomeStatementPerPeriod(localStart.toString(),localEnd.toString())
			BigDecimal totalRevenue = 0.00
			BigDecimal totalCostOfSales = 0.00
			BigDecimal totalExpenses = 0.00
			BigDecimal totalDiscounts = 0.00
			BigDecimal totalOtherIncome = 0.00
			BigDecimal totalFinanceCost = 0.00

			if(incomeStatementDtoList) {

				(incomeStatementDtoList as List<IncomeStatementDto>).each {
					it ->
						LocalDate transDate = LocalDate.parse(it.transactionDate)

						if (it.accountType.equalsIgnoreCase('COST_OF_SALE')) {
							totalCostOfSales = totalCostOfSales + it.amount
						} else if (it.accountType.equalsIgnoreCase('REVENUE')) {
							String[] otherInCodes = ['400060','400070','400090']
							//                                DISCOUNT AND ALLOWANCE
							if(it.code.equalsIgnoreCase('400080')){
								totalDiscounts = totalDiscounts + it.amount
							}
							else if(Arrays.asList(otherInCodes).contains(it.code)){
								totalOtherIncome = totalOtherIncome + it.amount
							}
							else {
								totalRevenue = totalRevenue + it.amount
							}
						} else {
							String[] otherInCodes = ['600420']
							//                              INTEREST EXPENSE
							if(Arrays.asList(otherInCodes).contains(it.code)){
								totalFinanceCost = totalFinanceCost + it.amount
							}
							else {
								totalExpenses = totalExpenses + it.amount
							}
						}
				}
			}
			BigDecimal grossProfit = 0.00
			BigDecimal grossIncome = 0.00
			BigDecimal netOpIncome = 0.00
			BigDecimal netProfit = 0.00
			BigDecimal netRevenue = 0.00

			netRevenue = totalRevenue - totalDiscounts
			grossProfit = netRevenue - totalCostOfSales
			grossIncome = grossProfit + totalOtherIncome
			netOpIncome = grossIncome - totalExpenses
			netProfit = netOpIncome - totalFinanceCost

			FinancialYearSavedNetAmounts balanceSheetSavedAmounts = new FinancialYearSavedNetAmounts()
			balanceSheetSavedAmounts.netProfit = netProfit
			balanceSheetSavedAmounts.year = year.toString()
			FinancialYearSavedNetAmounts.save(balanceSheetSavedAmounts)
			return balanceSheetSavedAmounts
		}

	}
}
