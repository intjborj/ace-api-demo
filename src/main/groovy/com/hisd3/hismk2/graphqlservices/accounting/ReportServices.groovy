package com.hisd3.hismk2.graphqlservices.accounting

import com.fasterxml.jackson.databind.ObjectMapper
import com.google.gson.Gson
import com.hisd3.hismk2.domain.accounting.BalanceSheetSavedAmounts
import com.hisd3.hismk2.domain.accounting.Fiscal
import com.hisd3.hismk2.domain.accounting.SavedReports
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.memoization.Memoize
import com.hisd3.hismk2.services.EntityObjectMapperService
import com.hisd3.hismk2.services.GeneratorService
import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.csv.CSVFormat
import org.apache.commons.csv.CSVPrinter
import org.apache.commons.lang3.StringUtils
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.nio.ByteBuffer
import java.nio.charset.StandardCharsets
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters

@Canonical
class IncomeStatementDto {
    String transactionDate
    String code
    String account
    String normalSide
    String accountType
    BigDecimal amount
}


@Canonical
class  ISPeriodDateRangeChild {
    Integer key
    String label
    LocalDate start
    LocalDate end
}


@Canonical
class  ISPeriodRangeParent {
    List<ISPeriodDateRangeChild> periodDateRangeChildList
    String start
    String end
}

@Canonical
class TrialBalanceDto {
    String code
    String account
    String normalSide
    BigDecimal debit
    BigDecimal credit
    BigDecimal balance
    String subAccounts
    String subsubAccounts
    Boolean bold
    Boolean alwaysShow
    Boolean boldUnderlined
    Boolean italic
    Integer rows
}


@Canonical
class TrialBalanceDto2 {
    String code
    String account
    String normalSide
    BigDecimal debit
    BigDecimal credit
    BigDecimal balance
    String mainCode
    String subCode
    Integer rows
}

@Canonical
class  ReportDetailsDto {
    String code
    String account
    String transactionDate
    String particulars
    String entityName
    String invoiceSoaReference
    BigDecimal debit
    BigDecimal credit
    BigDecimal balance
}

@Canonical
class ReportDtoPage {
    List <Object> list
    String code
    String account
}

@Canonical
class ReportDetailsPageDto {
    List<ReportDetailsDto> content
    String code
    String account
}

@Canonical
class IncomeStatementAccount {
    String code
    String account
}

@Canonical
class IncomeStatementPage {
    List<IncomeStatementAccount> revenue
    List<IncomeStatementAccount> expenses
    List<IncomeStatementAccount> costOfSales
    List<IncomeStatementAccount> discounts
    List<IncomeStatementAccount> otherIncome
    List<IncomeStatementAccount> financeCost

    Map<String,Map<String,BigDecimal>> mapRevenue
    Map<String,Map<String,BigDecimal>> mapExpenses
    Map<String,Map<String,BigDecimal>> mapCostOfSales
    Map<String,Map<String,BigDecimal>> mapDiscounts
    Map<String,Map<String,BigDecimal>> mapOtherIncome
    Map<String,Map<String,BigDecimal>> mapFinanceCost

    Map<String,Map<String,Object>> mapTotalRevenue

    List<ISPeriodDateRangeChild> isPeriodDateRangeChildList

    Map<String,BigDecimal> totalRevenue
    Map<String,BigDecimal> totalCostOfSales
    Map<String,BigDecimal> totalExpenses
    Map<String,BigDecimal> totalOtherIncome
    Map<String,BigDecimal> totalDiscounts
    Map<String,BigDecimal> totalFinanceCost
    Map<String,BigDecimal> netRevenue
    Map<String,BigDecimal> grossIncome
    Map<String,BigDecimal> grossProfit
    Map<String,BigDecimal> netOpIncome
    Map<String,BigDecimal> netProfit
    String period
}


@Canonical
class BalanceSheetPage {
    // Assets
        // Current Assets
        List<IncomeStatementAccount> cashEquiv
        List<IncomeStatementAccount> tradeReceivable
        List<IncomeStatementAccount> allowDbtAcc
        List<IncomeStatementAccount> advances
        List<IncomeStatementAccount> inventory
        List<IncomeStatementAccount> toolsSupplies
        List<IncomeStatementAccount> inputTax
        List<IncomeStatementAccount> prePayments
        List<IncomeStatementAccount> otherCurrAss
        // Non Current Assets
        List<IncomeStatementAccount> loansReceivable
        List<IncomeStatementAccount> propPlantEquipment
        List<IncomeStatementAccount> accumulatedDep
        List<IncomeStatementAccount> constructionInProg
        List<IncomeStatementAccount> intangibleAssets
        List<IncomeStatementAccount> defferedTaxAssets
        List<IncomeStatementAccount> otherNonCurrentAssets

    // Map Assets
        // Current Assets
        Map<String,Map<String,BigDecimal>> mapCashEquiv
        Map<String,Map<String,BigDecimal>> mapTradeReceivable
        Map<String,Map<String,BigDecimal>> mapAllowDbtAcc
        Map<String,Map<String,BigDecimal>> mapAdvances
        Map<String,Map<String,BigDecimal>> mapInventory
        Map<String,Map<String,BigDecimal>> mapToolsSupplies
        Map<String,Map<String,BigDecimal>> mapInputTax
        Map<String,Map<String,BigDecimal>> mapPrePayments
        Map<String,Map<String,BigDecimal>> mapOtherCurrAss
        // Non Current Assets
        Map<String,Map<String,BigDecimal>> mapLoansReceivable
        Map<String,Map<String,BigDecimal>> mapPropPlantEquipment
        Map<String,Map<String,BigDecimal>> mapAccumulatedDep
        Map<String,Map<String,BigDecimal>> mapConstructionInProg
        Map<String,Map<String,BigDecimal>> mapIntangibleAssets
        Map<String,Map<String,BigDecimal>> mapDefferedTaxAssets
        Map<String,Map<String,BigDecimal>> mapOtherNonCurrentAssets

    // Total of Assets
        // Current Assets
        Map<String,BigDecimal> totalCashEquiv
        Map<String,BigDecimal> totalTradeReceivable
        Map<String,BigDecimal> totalAllowDbtAcc
        Map<String,BigDecimal> totalAdvances
        Map<String,BigDecimal> totalInventory
        Map<String,BigDecimal> totalToolsSupplies
        Map<String,BigDecimal> totalInputTax
        Map<String,BigDecimal> totalPrePayments
        Map<String,BigDecimal> totalOtherCurrAss
        // Non Current Assets
        Map<String,BigDecimal> totalLoansReceivable
        Map<String,BigDecimal> totalPropPlantEquipment
        Map<String,BigDecimal> totalAccumulatedDep
        Map<String,BigDecimal> totalConstructionInProg
        Map<String,BigDecimal> totalIntangibleAssets
        Map<String,BigDecimal> totalDefferedTaxAssets
        Map<String,BigDecimal> totalOtherNonCurrentAssets

    // LIABILITIES
        // CURRENT LIABILITIES
        List<IncomeStatementAccount> dueToDoctors
        List<IncomeStatementAccount> accountsPayable
        List<IncomeStatementAccount> nonPayableCurrent
        List<IncomeStatementAccount> accruedInterestPayable
        List<IncomeStatementAccount> accruedSalariesAndWagesPayable
        List<IncomeStatementAccount> accruedVLSPayable
        List<IncomeStatementAccount> benefits
        List<IncomeStatementAccount> advancesToEmp
        List<IncomeStatementAccount> taxesPayable
        List<IncomeStatementAccount> permitAndLiscensPayables
        List<IncomeStatementAccount> otherCurrentPayables

        Map<String,Map<String,BigDecimal>> dueToDoctorsMap
        Map<String,Map<String,BigDecimal>> accountsPayableMap
        Map<String,Map<String,BigDecimal>> nonPayableCurrentMap
        Map<String,Map<String,BigDecimal>> accruedInterestPayableMap
        Map<String,Map<String,BigDecimal>> accruedSalariesAndWagesPayableMap
        Map<String,Map<String,BigDecimal>> accruedVLSPayableMap
        Map<String,Map<String,BigDecimal>> benefitsMap
        Map<String,Map<String,BigDecimal>> advancesToEmpMap
        Map<String,Map<String,BigDecimal>> taxesPayableMap
        Map<String,Map<String,BigDecimal>> permitAndLiscensPayablesMap
        Map<String,Map<String,BigDecimal>> otherCurrentPayablesMap

        Map<String,BigDecimal> dueToDoctorsTotal
        Map<String,BigDecimal> accountsPayableTotal
        Map<String,BigDecimal> nonPayableCurrentTotal
        Map<String,BigDecimal> accruedInterestPayableTotal
        Map<String,BigDecimal> accruedSalariesAndWagesPayableTotal
        Map<String,BigDecimal> accruedVLSPayableTotal
        Map<String,BigDecimal> benefitsTotal
        Map<String,BigDecimal> advancesToEmpTotal
        Map<String,BigDecimal> taxesPayableTotal
        Map<String,BigDecimal> permitAndLiscensPayablesTotal
        Map<String,BigDecimal> otherCurrentPayablesTotal

        //NON CURRENT LIABILITIES
        List<IncomeStatementAccount> advancesToShareHolders
        List<IncomeStatementAccount> notesPayableNonCurrent
        List<IncomeStatementAccount> otherNonCurrentLiabilities

        Map<String,Map<String,BigDecimal>> advancesToShareHoldersMap
        Map<String,Map<String,BigDecimal>> notesPayableNonCurrentMap
        Map<String,Map<String,BigDecimal>> otherNonCurrentLiabilitiesMap

        Map<String,BigDecimal> advancesToShareHoldersTotal
        Map<String,BigDecimal> notesPayableNonCurrentTotal
        Map<String,BigDecimal> otherNonCurrentLiabilitiesTotal

    // SHARE HOLDER EQUITY
        List<IncomeStatementAccount> shareCapital
        List<IncomeStatementAccount> additionalPaidCapital
        List<IncomeStatementAccount> retainedEarnings
        List<IncomeStatementAccount> dividends

        Map<String,Map<String,BigDecimal>> shareCapitalMap
        Map<String,Map<String,BigDecimal>> additionalPaidCapitalMap
        Map<String,Map<String,BigDecimal>> retainedEarningsMap
        Map<String,Map<String,BigDecimal>> dividendsMap

        Map<String,BigDecimal> shareCapitalTotal
        Map<String,BigDecimal> additionalPaidCapitalTotal
        Map<String,BigDecimal> retainedEarningsTotal
        Map<String,BigDecimal> dividendsTotal

    List<ISPeriodDateRangeChild> isPeriodDateRangeChildList

        Map<String,BigDecimal> assets
        Map<String,BigDecimal> liabilities
        Map<String,BigDecimal> equity
        Map<String,BigDecimal> totalLiabilitiesAndEquity
        Map<String,BigDecimal> totalCurrentAssets
        Map<String,BigDecimal> totalNonCurrentAssets
        Map<String,BigDecimal> totalCurrentLiabilities
        Map<String,BigDecimal> totalNonCurrentLiabilities
        String period
}

@Canonical
class ISListOfAccounts {
    String code
    String header
    String description
    Map<String,BigDecimal> amount
}

@Canonical
class IncomeStatementFormat {
    Integer order
    String header
    List<ISListOfAccounts> listOfAccounts
    BigDecimal total
}

@Canonical
class MapOfAccountsReturn {
    Map<String,Map<String,BigDecimal>> mapAccounts
    List<IncomeStatementAccount> iSAccount
    Map<String,BigDecimal> totalAccount
}



@Transactional(rollbackOn = [Exception.class])
@Component
@GraphQLApi
class ReportService {

    @Autowired
    FiscalServices fiscalServices

    @Autowired
    EntityObjectMapperService entityObjectMapperService

    @Autowired
    GeneratorService generatorService

    @Autowired
    EntityManager entityManager

    @Autowired
    BalanceSheetsSavedAmountsService balanceSheetsSavedAmountsService

    @GraphQLMutation(name="getTrialBalanceSummary")
    List<TrialBalanceDto2> getTrialBalanceSummary(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end
    )
    {
       return entityManager.createNativeQuery("""
            select     
               ld.journal_account->'motherAccount'->>'code' as "code",
               ld.journal_account->'motherAccount'->>'description' as "account",
               ld.journal_account->'motherAccount'->>'normalSide' as "normalSide",
               sum(ld.debit) as "debit" ,
               sum(ld.credit) as "credit",
               case  
                when (coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)) < 0 then ((coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0))*-1)
                else  coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)
               end as "balance",
               ld.journal_account->'motherAccount'->>'code' as "mainCode",
               cast(coalesce(count(DISTINCT ld.journal_account->'subAccount'->>'code'),0) as Int) as "rows"
               from accounting.ledger_date ld 
               left join accounting.header_ledger hl on hl.id  = ld."header" 
               where
               hl.approved_by is not null
               and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
               group  by 
               ld.journal_account->'motherAccount'->>'code',
               ld.journal_account->'motherAccount'->>'description',
               ld.journal_account->'motherAccount'->>'normalSide'
               order  by ld.journal_account->'motherAccount'->>'code'
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(TrialBalanceDto2.class))
                .getResultList()
    }

    @GraphQLQuery(name = "getTrialBalanceSubAccountsSummary")
    List<TrialBalanceDto2> getTrialBalanceSubAccountsSummary(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end,
            @GraphQLArgument(name = "code") String  code
    )
    {
        return  entityManager.createNativeQuery("""
           select
            concat(ld.journal_account->'motherAccount'->>'code','-',ld.journal_account->'subAccount'->>'code') as "code",
            concat(ld.journal_account->'motherAccount'->>'description','-',ld.journal_account->'subAccount'->>'description') as "account",
            ld.journal_account->'motherAccount'->>'normalSide' as "normalSide",
               sum(ld.debit) as "debit" ,
               sum(ld.credit) as "credit",
               case  
                when (coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)) < 0 then ((coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0))*-1)
                else  coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)
               end as "balance",
            ld.journal_account->'motherAccount'->>'code' as "mainCode",
            ld.journal_account->'subAccount'->>'code' as "subCode",
            cast(coalesce(count(DISTINCT ld.journal_account->>'code'),0) as Int) as "rows"
            from accounting.ledger_date ld 
            left join accounting.header_ledger hl on hl.id  = ld."header" 
               where
              ld.journal_account->'motherAccount'->>'code' = :code
               and
               hl.approved_by is not null
               and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
            group  by 
            concat(ld.journal_account->'motherAccount'->>'code','-',ld.journal_account->'subAccount'->>'code'),
            concat(ld.journal_account->'motherAccount'->>'description','-',ld.journal_account->'subAccount'->>'description'),
            ld.journal_account->'subAccount'->>'code',
            ld.journal_account->'motherAccount'->>'code',
            ld.journal_account->'motherAccount'->>'normalSide'
            order  by concat(ld.journal_account->'motherAccount'->>'code','-',ld.journal_account->'subAccount'->>'code')
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .setParameter('code',code)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(TrialBalanceDto2.class))
                .getResultList();

    }

    @GraphQLQuery(name = "getTrialBalanceSubSubAccountsSummary")
    List<TrialBalanceDto2> getTrialBalanceSubSubAccountsSummary(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end,
            @GraphQLArgument(name = "code") String  code,
            @GraphQLArgument(name = "subCode") String  subCode
    )
    {
        return  entityManager.createNativeQuery("""
           select
            concat(ld.journal_account->'motherAccount'->>'code','-',ld.journal_account->'subAccount'->>'code','-',ld.journal_account->>'code') as "code",
            concat(ld.journal_account->'motherAccount'->>'description','-',ld.journal_account->'subAccount'->>'description','-',ld.journal_account->>'description') as "account",
               ld.journal_account->'motherAccount'->>'normalSide' as "normalSide",
               sum(ld.debit) as "debit" ,
               sum(ld.credit) as "credit",
               case  
                when (coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)) < 0 then ((coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0))*-1)
                else  coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)
               end as "balance"
            from accounting.ledger_date ld 
            left join accounting.header_ledger hl on hl.id  = ld."header" 
               where
                ld.journal_account->'motherAccount'->>'code' = :code
                and
                ld.journal_account->'subAccount'->>'code' = :subCode
               and
               hl.approved_by is not null
               and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
            group  by 
            concat(ld.journal_account->'motherAccount'->>'code','-',ld.journal_account->'subAccount'->>'code','-',ld.journal_account->>'code'),
            concat(ld.journal_account->'motherAccount'->>'description','-',ld.journal_account->'subAccount'->>'description','-',ld.journal_account->>'description'),
            ld.journal_account->'motherAccount'->>'normalSide'
            order  by concat(ld.journal_account->'motherAccount'->>'code','-',ld.journal_account->'subAccount'->>'code','-',ld.journal_account->>'code')
         
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .setParameter('code',code)
                .setParameter('subCode',subCode)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(TrialBalanceDto2.class))
                .getResultList();

    }

    @GraphQLQuery(name = "trialBalanceAccounts")
    List<TrialBalanceDto> trialBalanceAccounts(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end
    )
    {
       return  entityManager.createNativeQuery("""
           select 
                "code",
                "mother" as "account",
                "normalSide",
                sum("subDebit") as "debit",
                sum("subCredit") as "credit",
                (coalesce(sum("subDebit"),0)-coalesce(sum("subCredit"),0)) as "balance",
                cast(jsonb_agg(jsonb_build_object(
                'code',concat("code",'-',"subCode"),
                'account',concat("mother",'-',"subAccount") ,
                'debit',"subDebit",
                'credit',"subCredit",
                'balance',"subBalance",
                'subsubAccounts',"subsubAccounts"
                )) as text) as "subAccounts"
            from
                (select 
                "code",
                "mother",
                "normalSide",
                "subCode",
                cast("subAccountId" as text) as "subAccountId",
                "subAccount",
                sum("debit") as "subDebit",
                sum("credit") as "subCredit",
                (coalesce(sum("debit"),0)-coalesce(sum("credit"),0)) as "subBalance",
                jsonb_agg(jsonb_build_object('code',"subsubCode",'id',"subsubAccountId",'account',"subsubAccount",'debit',"debit",'credit',"credit",'balance',"balance",'details',"details")) as "subsubAccounts"
                from
                    (select
                       ld.journal_account->'motherAccount'->>'description' as "mother",
                       ld.journal_account->'motherAccount'->>'code' as "code",
                       ld.journal_account->'motherAccount'->>'normalSide' as "normalSide",
                       cast(ld.journal_account->'subAccount'->>'id' as text) as "subAccountId",
                       ld.journal_account->'subAccount'->>'code' as "subCode",
                       ld.journal_account->'subAccount'->>'description' as "subAccount",
                       cast(ld.journal_account->'subAccount'->>'id' as text) as "subsubAccountId",
                       ld.journal_account->'code' as "subsubCode",
                       ld.journal_account->'description' as "subsubAccount",
                       sum(ld.debit) as "debit" ,
                       sum(ld.credit) as "credit",
                       (coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)) as "balance",
                       jsonb_agg(jsonb_build_object('particulars',hl.particulars,'entityName',hl.entity_name,'reference',hl.invoice_soa_reference,'debit',ld.debit,'credit',ld.credit,'balance',coalesce(ld.debit,0)-coalesce(ld.credit,0))) as details
               from accounting.ledger_date ld 
               left join accounting.header_ledger hl on hl.id  = ld."header" 
               where
               hl.approved_by is not null
               and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
               group  by "mother","subAccount","subsubAccount","subAccountId","subsubAccountId","normalSide","code","subCode","subsubCode"
               order  by "mother") as subsubaccount
                group by 
                "code",
                "mother",
                "subCode",
                "subAccount",
                "subAccountId",
                "normalSide"
                order by
                "mother",
                "subAccount") as motheraccount
            group by 
            "code",
            "mother",
            "normalSide"
            order by "code"
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(TrialBalanceDto.class))
                .getResultList();

    }

    @GraphQLQuery(name = "getTrialBalanceByCode")
    List<ReportDetailsDto> getTrialBalanceByCode(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end,
            @GraphQLArgument(name = "code") String  code
    )
    {
        return  entityManager.createNativeQuery("""
           select
                cast(ld.journal_account->>'code' as text) as "code",
                ld.journal_account->>'description' as "account",
                to_char(date(hl.transaction_date + interval '8 hour'),'YYYY-MM-DD') as "transactionDate",
                hl.particulars,
                hl.entity_name as "entityName",
                hl.invoice_soa_reference as "invoiceSoaReference",
                ld.debit,
                ld.credit,
                coalesce(ld.debit,0)-coalesce(ld.credit,0) as balance
            from accounting.ledger_date ld
                left join accounting.header_ledger hl on hl.id  = ld."header"
            where
                hl.approved_by is not null
                and cast(ld.journal_account->>'code' as varchar) = :code
                and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
            order  by hl.transaction_date
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .setParameter('code',code)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(ReportDetailsDto.class))
                .getResultList();

    }

    @GraphQLQuery(name = "trialBalanceAccountsDetails")
    ReportDetailsPageDto trialBalanceAccountsDetails(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end,
            @GraphQLArgument(name = "code") String  code
    )
    {

        List<ReportDetailsDto> tbl =  getTrialBalanceByCode(start,end,code)
        if(tbl){
            return new ReportDetailsPageDto(tbl,tbl[0].code,tbl[0].account)
        }
        return new ReportDetailsPageDto(tbl,"","")
    }

    @GraphQLQuery(name = "getIncomeStatementByCode")
    List<ReportDetailsDto> getIncomeStatementByCode(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end,
            @GraphQLArgument(name = "code") String  code
    )
    {
        return  entityManager.createNativeQuery("""
           select 
                cast(ld.journal_account->>'code' as text) as "code",
                ld.journal_account->>'description' as "account",
                to_char(date(hl.transaction_date + interval '8 hour'),'YYYY-MM-DD') as "transactionDate",
                hl.particulars,
                hl.entity_name as "entityName",
                hl.invoice_soa_reference as "invoiceSoaReference",
                ld.debit,
                ld.credit,
                coalesce(ld.debit,0)-coalesce(ld.credit,0) as balance
            from accounting.ledger_date ld 
            left join accounting.header_ledger hl on hl.id  = ld."header"
            left join accounting.chart_of_accounts coa on coa.account_code = ld.journal_account->'motherAccount'->>'code'
            where
            cast(ld.journal_account->'motherAccount'->>'code' as varchar) = :code and
            coa.account_type in ('REVENUE','COST_OF_SALE','EXPENSE') and
            hl.approved_by is not null
            and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
            order  by hl.transaction_date
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .setParameter('code',code)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(ReportDetailsDto.class))
                .getResultList();

    }

    @GraphQLQuery(name = "incomeStatementDetails")
    ReportDetailsPageDto incomeStatementDetails(
            @GraphQLArgument(name = "start") String  start,
            @GraphQLArgument(name = "end") String  end,
            @GraphQLArgument(name = "code") String  code
    )
    {
        List<ReportDetailsDto> tbl =  getIncomeStatementByCode(start,end,code)
        if(tbl){
            return new ReportDetailsPageDto(tbl,tbl[0].code,tbl[0].account)
        }
        return new ReportDetailsPageDto(tbl,"","")
    }

    List<IncomeStatementDto> getIncomeStatementPerPeriod (String start,String end){
        return entityManager.createNativeQuery("""
           select 
                "transactionDate",
                "code",
                "mother" as "account",
                "normalSide",
                "accountType",
                sum("amount") as "amount"
            from
                (select 
                "transactionDate",
                "code",
                "mother",
                "normalSide",
                "accountType",
                sum("amount") as "amount"
                from
                    (select 
                        to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') as "transactionDate",
                        ld.journal_account->'motherAccount'->>'description' as "mother",
                        ld.journal_account->'motherAccount'->>'code' as "code",
                        ld.journal_account->'motherAccount'->>'normalSide' as "normalSide",
                        cast(ld.journal_account->'subAccount'->>'id' as text) as "subAccountId",
                        ld.journal_account->'subAccount'->>'code' as "subCode",
                        ld.journal_account->'subAccount'->>'description' as "subAccount",
                        cast(ld.journal_account->'subAccount'->>'id' as text) as "subsubAccountId",
                        ld.journal_account->'code' as "subsubCode",
                        ld.journal_account->'description' as "subsubAccount",
                        coalesce(sum(ld.debit),0) as "debit",
                        coalesce(sum(ld.credit),0) as "credit",
                        case 
                            when ld.journal_account->'motherAccount'->>'normalSide' = 'DEBIT' then (coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)) 
                        else  (coalesce(sum(ld.credit),0)-coalesce(sum(ld.debit),0)) 
                        end as "amount",
                        coa.account_type as "accountType"
                        from accounting.ledger_date ld 
                        left join accounting.header_ledger hl on hl.id  = ld."header"
                        left join accounting.chart_of_accounts coa on coa.account_code = ld.journal_account->'motherAccount'->>'code'
                        where
                        coa.account_type in ('REVENUE','COST_OF_SALE','EXPENSE') and
                        hl.approved_by is not null
                        and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
                        group  by "mother","subAccount","subsubAccount","subAccountId","subsubAccountId","normalSide","code","subCode","subsubCode", coa.account_type, "transactionDate" 
                        order  by "mother") as subsubaccount
                group by 
                "code",
                "mother",
                "subCode",
                "subAccount",
                "subAccountId",
                "normalSide",
                "accountType",
                "transactionDate"
                order by
                "mother",
                "subAccount") as motheraccount
            group by 
            "code",
            "mother",
            "normalSide",
            "accountType",
            "transactionDate"
            order by "code"
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(IncomeStatementDto.class))
                .getResultList();
    }

    static String getCalculationType(LocalDate start, LocalDate end){
        LocalDate firstDayOfMonth = start.with(TemporalAdjusters.firstDayOfMonth())
        LocalDate lastDayOfStartMonth = start.with(TemporalAdjusters.lastDayOfMonth())
        LocalDate lastDayOfEndMonth = end.with(TemporalAdjusters.lastDayOfMonth())

        if(((start == firstDayOfMonth) && (lastDayOfStartMonth == end)) && (start.getYear() == end.getYear())){
            return  'monthly'
        }
        else if(((start == firstDayOfMonth) && (lastDayOfEndMonth == end)) && (start.getYear() == end.getYear()))
        {
            return  'yearly'
        }
        else if(start.getYear() != end.getYear())
        {
            return  'custom-yearly'
        }
        else {
            return  'daily'
        }
    }

    static String getPeriodLabel(String calculationType, LocalDate start, LocalDate end){
        def firstFormatter = DateTimeFormatter.ofPattern(" MMM YYYY")
        def secondFormatter = DateTimeFormatter.ofPattern("YYYY")
        def thirdFormatter = DateTimeFormatter.ofPattern("d MMM")
        def fourthFormatter = DateTimeFormatter.ofPattern("d MMM YYYY")

        switch (calculationType){
            case ('monthly'):
                return "${start.format(firstFormatter)}"
                break;
            case ('yearly'):
                return "${start.format(secondFormatter)}"
                break;
            case ('custom-yearly'):
                return "${start.format(fourthFormatter)}-${end.format(fourthFormatter)}"
                break;
            default:
                return "${start.format(thirdFormatter)}-${end.format(fourthFormatter)}"
                break;
        }

    }

    static LocalDate getDateStartEnd(String calculationType, LocalDate start, LocalDate end, List<ISPeriodDateRangeChild> periodList, String returnType){
        LocalDate monthStart
        LocalDate monthEnd
        Period period = Period.between(start,end)
        Integer diffMonths = Math.abs(period.getMonths())
        Integer diffDays = Math.abs(period.getDays())
        Integer diffYears = Math.abs(period.getYears())

        switch (calculationType){
            case ('monthly') :
                monthStart = periodList[0].start.minusMonths(1)
                monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth())
                break;
            case ('yearly') :
                monthStart = periodList[0].start.minusYears(1).with(TemporalAdjusters.firstDayOfMonth())
                monthEnd = periodList[0].end.minusYears(1).with(TemporalAdjusters.lastDayOfMonth())
                break;
            default:
                monthEnd = periodList[0].start.minusDays(1)
                monthStart = monthEnd.minusDays(diffDays)
                if(diffMonths > 0){
                    monthStart =  monthStart.minusMonths(diffMonths)
                }
                if(diffYears > 0){
                    monthStart =  monthStart.minusYears(diffYears)
                }
                break;
        }

        if(returnType.equalsIgnoreCase('start'))
            return monthStart

        return monthEnd

    }

    static ISPeriodRangeParent getIncomeStatementPeriodRange(LocalDate start, LocalDate end, Integer numPeriod) {
        ISPeriodRangeParent periodRangeList = new ISPeriodRangeParent()
        String calculationType = getCalculationType(start,end)
        Integer i = 0

        List<ISPeriodDateRangeChild> periodDateRangeChildList = new ArrayList<ISPeriodDateRangeChild>()

        ISPeriodDateRangeChild isPeriodDateRangeChild = new ISPeriodDateRangeChild()
        isPeriodDateRangeChild.key = i++
        isPeriodDateRangeChild.label = getPeriodLabel(calculationType,start,end)
        isPeriodDateRangeChild.start = start
        isPeriodDateRangeChild.end = end
        periodDateRangeChildList.push(isPeriodDateRangeChild)

        while (numPeriod > i){
            ISPeriodDateRangeChild isPeriodDateRangeChildWhile = new ISPeriodDateRangeChild()
            isPeriodDateRangeChildWhile.start = getDateStartEnd(calculationType, start, end, periodDateRangeChildList, 'start')
            isPeriodDateRangeChildWhile.end = getDateStartEnd(calculationType, start, end, periodDateRangeChildList, 'end')
            isPeriodDateRangeChildWhile.label = getPeriodLabel(calculationType,isPeriodDateRangeChildWhile.start,isPeriodDateRangeChildWhile.end)
            isPeriodDateRangeChildWhile.key = i++
            periodDateRangeChildList.push(isPeriodDateRangeChildWhile)
        }

        periodRangeList.periodDateRangeChildList = periodDateRangeChildList
        periodRangeList.start = periodDateRangeChildList[0].start
        periodRangeList.end = periodDateRangeChildList[i-1].end
        return  periodRangeList
    }

    @GraphQLQuery(name = "getIncomeStatements")
    IncomeStatementPage getIncomeStatements(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period
    ){
        def formatter = DateTimeFormatter.ofPattern(" d MMMM YYYY")
        LocalDate localStart = LocalDate.parse(start)
        LocalDate localEnd = LocalDate.parse(end)

        ISPeriodRangeParent periodRange = getIncomeStatementPeriodRange(localStart,localEnd,period+1)

        IncomeStatementPage incomeStatementPage = new IncomeStatementPage()

        List<IncomeStatementDto> incomeStatementDtoList = getIncomeStatementPerPeriod(periodRange.start,periodRange.end)

        incomeStatementPage.period = "${localStart.format(formatter)} to ${localEnd.format(formatter)}"

        Map<String,Map<String,BigDecimal>> mapRevenue = [:]

        Map<String,Map<String,BigDecimal>> mapExpenses = [:]

        Map<String,Map<String,BigDecimal>> mapCostOfSales = [:]

        Map<String,Map<String,BigDecimal>> mapDiscounts = [:]

        Map<String,Map<String,BigDecimal>> mapOtherIncome = [:]

        Map<String,Map<String,BigDecimal>> mapFinanceCost = [:]

        List<IncomeStatementAccount> revenue = new ArrayList<IncomeStatementAccount>()
        List<IncomeStatementAccount> expenses = new ArrayList<IncomeStatementAccount>()
        List<IncomeStatementAccount> costOfSales = new ArrayList<IncomeStatementAccount>()
        List<IncomeStatementAccount> discounts = new ArrayList<IncomeStatementAccount>()
        List<IncomeStatementAccount> otherIncome = new ArrayList<IncomeStatementAccount>()
        List<IncomeStatementAccount> financeCost = new ArrayList<IncomeStatementAccount>()

        Map<String,BigDecimal> totalRevenue = [:]
        Map<String,BigDecimal> totalCostOfSales = [:]
        Map<String,BigDecimal> totalExpenses = [:]
        Map<String,BigDecimal> totalDiscounts = [:]
        Map<String,BigDecimal> totalOtherIncome = [:]
        Map<String,BigDecimal> totalFinanceCost = [:]

        List<ISPeriodDateRangeChild> isPeriodDateRangeChildList = new ArrayList<ISPeriodDateRangeChild>()
        Integer count = 0
        periodRange.periodDateRangeChildList = periodRange.periodDateRangeChildList.toSorted{ a, b->a.key <=> b.key }
        periodRange.periodDateRangeChildList.each {
            totalRevenue[it.key as String] = 0.00
            totalCostOfSales[it.key as String] = 0.00
            totalExpenses[it.key as String] = 0.00

            ISPeriodDateRangeChild isPeriodDateRangeChild = new ISPeriodDateRangeChild()
            isPeriodDateRangeChild.key = count
            isPeriodDateRangeChild.label = it.label
            isPeriodDateRangeChild.start = it.start
            isPeriodDateRangeChild.end = it.end
            isPeriodDateRangeChildList.push(isPeriodDateRangeChild)
            count++

            if((count+1)%2){
                ISPeriodDateRangeChild isPeriodDateRangeChild1 = new ISPeriodDateRangeChild()
                isPeriodDateRangeChild1.key = count++
                isPeriodDateRangeChild1.label = 'Increase (Decrease)'
                isPeriodDateRangeChildList.push(isPeriodDateRangeChild1)

                ISPeriodDateRangeChild isPeriodDateRangeChild2 = new ISPeriodDateRangeChild()
                isPeriodDateRangeChild2.key = count++
                isPeriodDateRangeChild2.label = '%'
                isPeriodDateRangeChildList.push(isPeriodDateRangeChild2)
            }

        }

        isPeriodDateRangeChildList = isPeriodDateRangeChildList.toSorted{ a, b->a.key <=> b.key }

        if(incomeStatementDtoList) {

            (incomeStatementDtoList as List<IncomeStatementDto>).each {
                it ->
                    LocalDate transDate = LocalDate.parse(it.transactionDate)

                    if (it.accountType.equalsIgnoreCase('COST_OF_SALE')) {

                        isPeriodDateRangeChildList.each {
                            pDR ->
                                def compareStart = transDate <=> pDR.start
                                def compareEnd = transDate <=> pDR.end

                                if (!mapCostOfSales[it.code]) {
                                    costOfSales.push(new IncomeStatementAccount(code: it.code, account: it.account))
                                    Map<String,BigDecimal> mapIncomeCoS = [:]
                                    mapIncomeCoS[pDR.key.toString()] = 0.00
                                    mapCostOfSales[it.code] = mapIncomeCoS as Map<String,BigDecimal>
                                }

                                if (compareStart >= 0 && compareEnd <= 0) {
                                    if(!mapCostOfSales[it.code][pDR.key.toString()]){
                                        mapCostOfSales[it.code][pDR.key.toString()] = 0.00
                                    }
                                    mapCostOfSales[it.code][pDR.key.toString()] = mapCostOfSales[it.code][pDR.key.toString()] + it.amount
                                    totalCostOfSales[pDR.key.toString()] = totalCostOfSales[pDR.key.toString()] ? totalCostOfSales[pDR.key.toString()] : 0.00
                                    totalCostOfSales[pDR.key.toString()] = totalCostOfSales[pDR.key.toString()] + it.amount
                                }

//                                For comparison every 2 consecutive dates
                                if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                    Integer keys = pDR.key
                                    BigDecimal firstVal = mapCostOfSales[it.code][(keys-2).toString()] ? mapCostOfSales[it.code][(keys-2).toString()] : 0.00
                                    BigDecimal secondVal = mapCostOfSales[it.code][(keys-1).toString()] ? mapCostOfSales[it.code][(keys-1).toString()] : 0.00
                                    mapCostOfSales[it.code][pDR.key.toString()] = firstVal - secondVal

                                    BigDecimal firstValTotal = totalCostOfSales[(keys-2).toString()] ? totalCostOfSales[(keys-2).toString()] : 0.00
                                    BigDecimal secondValTotal = totalCostOfSales[(keys-1).toString()] ? totalCostOfSales[(keys-1).toString()] : 0.00
                                    totalCostOfSales[pDR.key.toString()] = firstValTotal - secondValTotal
                                }

                                if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
                                    Integer keys = pDR.key
                                    BigDecimal firstVal = mapCostOfSales[it.code][(keys-3).toString()] ? mapCostOfSales[it.code][(keys-3).toString()] : 0.00
                                    BigDecimal secondVal = mapCostOfSales[it.code][(keys-2).toString()] ? mapCostOfSales[it.code][(keys-2).toString()] : 0.00
                                    if(secondVal > 0) {
                                        mapCostOfSales[it.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
                                    }
                                    else {
                                        mapCostOfSales[it.code][pDR.key.toString()] = 0.00 as BigDecimal
                                    }

                                    BigDecimal firstValTotal = mapCostOfSales[(keys-3).toString()] ? mapCostOfSales[(keys-3).toString()] : 0.00
                                    BigDecimal secondValTotal = mapCostOfSales[(keys-2).toString()] ? mapCostOfSales[(keys-2).toString()] : 0.00
                                    if(secondValTotal > 0) {
                                        mapCostOfSales[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
                                    }
                                    else {
                                        mapCostOfSales[pDR.key.toString()] = 0.00
                                    }
                                }
                        }
                    } else if (it.accountType.equalsIgnoreCase('REVENUE')) {

                        isPeriodDateRangeChildList.each {
                            pDR ->
                                def compareStart = transDate <=> pDR.start
                                def compareEnd = transDate <=> pDR.end
                                String[] otherInCodes = ['400060','400070','400090']

//                                DISCOUNT AND ALLOWANCE
                                if(it.code.equalsIgnoreCase('400080')){
                                    totalDiscounts[pDR.key.toString()] = totalDiscounts[pDR.key.toString()] ? totalDiscounts[pDR.key.toString()] : 0.00

                                    if (!mapDiscounts[it.code]) {
                                        discounts.push(new IncomeStatementAccount(code: it.code, account: it.account))
                                        Map<String,BigDecimal> mapIncomeDiscount = [:]
                                        mapIncomeDiscount[pDR.key.toString()] = 0.00
                                        mapDiscounts[it.code] = mapIncomeDiscount as Map<String,BigDecimal>
                                    }

                                    if (compareStart >= 0 && compareEnd <= 0) {
                                        if(!mapDiscounts[it.code][pDR.key.toString()]){
                                            mapDiscounts[it.code][pDR.key.toString()] = 0.00
                                        }
                                        mapDiscounts[it.code][pDR.key.toString()] = mapDiscounts[it.code][pDR.key.toString()] + it.amount
                                        totalDiscounts[pDR.key.toString()] = totalDiscounts[pDR.key.toString()] ? totalDiscounts[pDR.key.toString()] : 0.00
                                        totalDiscounts[pDR.key.toString()] = totalDiscounts[pDR.key.toString()] + it.amount
                                    }

                                    if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapDiscounts[it.code][(keys-2).toString()] ? mapDiscounts[it.code][(keys-2).toString()] : 0.00
                                        BigDecimal secondVal = mapDiscounts[it.code][(keys-1).toString()] ? mapDiscounts[it.code][(keys-1).toString()] : 0.00
                                        mapDiscounts[it.code][pDR.key.toString()] = firstVal - secondVal

                                        BigDecimal firstValTotal = totalDiscounts[(keys-2).toString()] ? totalDiscounts[(keys-2).toString()] : 0.00
                                        BigDecimal secondValTotal = totalDiscounts[(keys-1).toString()] ? totalDiscounts[(keys-1).toString()] : 0.00
                                        totalDiscounts[pDR.key.toString()] = firstValTotal - secondValTotal
                                    }

                                    if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapDiscounts[it.code][(keys-3).toString()] ? mapDiscounts[it.code][(keys-3).toString()] : 0.00
                                        BigDecimal secondVal = mapDiscounts[it.code][(keys-2).toString()] ? mapDiscounts[it.code][(keys-2).toString()] : 0.00
                                        if(secondVal > 0) {
                                            mapDiscounts[it.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
                                        }
                                        else {
                                            mapDiscounts[it.code][pDR.key.toString()] = 0.00 as BigDecimal
                                        }

                                        BigDecimal firstValTotal = mapDiscounts[(keys-3).toString()] ? mapDiscounts[(keys-3).toString()] : 0.00
                                        BigDecimal secondValTotal = mapDiscounts[(keys-2).toString()] ? mapDiscounts[(keys-2).toString()] : 0.00
                                        if(secondValTotal > 0) {
                                            mapDiscounts[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
                                        }
                                        else {
                                            mapDiscounts[pDR.key.toString()] = 0.00
                                        }
                                    }
                                }
                                else if(Arrays.asList(otherInCodes).contains(it.code)){
                                    totalOtherIncome[pDR.key.toString()] = totalOtherIncome[pDR.key.toString()] ? totalOtherIncome[pDR.key.toString()] : 0.00

                                    if (!mapOtherIncome[it.code]) {
                                        otherIncome.push(new IncomeStatementAccount(code: it.code, account: it.account))
                                        Map<String,BigDecimal> mapIncomeOther = [:]
                                        mapIncomeOther[pDR.key.toString()] = 0.00
                                        mapOtherIncome[it.code] = mapIncomeOther as Map<String,BigDecimal>
                                    }

                                    if (compareStart >= 0 && compareEnd <= 0) {
                                        if(!mapOtherIncome[it.code][pDR.key.toString()]){
                                            mapOtherIncome[it.code][pDR.key.toString()] = 0.00
                                        }
                                        mapOtherIncome[it.code][pDR.key.toString()] = mapOtherIncome[it.code][pDR.key.toString()] + it.amount
                                        totalOtherIncome[pDR.key.toString()] = totalOtherIncome[pDR.key.toString()] ? totalOtherIncome[pDR.key.toString()] : 0.00
                                        totalOtherIncome[pDR.key.toString()] = totalOtherIncome[pDR.key.toString()] + it.amount
                                    }

                                    if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapOtherIncome[it.code][(keys-2).toString()] ? mapOtherIncome[it.code][(keys-2).toString()] : 0.00
                                        BigDecimal secondVal = mapOtherIncome[it.code][(keys-1).toString()] ? mapOtherIncome[it.code][(keys-1).toString()] : 0.00
                                        mapOtherIncome[it.code][pDR.key.toString()] = firstVal - secondVal

                                        BigDecimal firstValTotal = totalOtherIncome[(keys-2).toString()] ? totalOtherIncome[(keys-2).toString()] : 0.00
                                        BigDecimal secondValTotal = totalOtherIncome[(keys-1).toString()] ? totalOtherIncome[(keys-1).toString()] : 0.00
                                        totalOtherIncome[pDR.key.toString()] = firstValTotal - secondValTotal
                                    }

                                    if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapOtherIncome[it.code][(keys-3).toString()] ? mapOtherIncome[it.code][(keys-3).toString()] : 0.00
                                        BigDecimal secondVal = mapOtherIncome[it.code][(keys-2).toString()] ? mapOtherIncome[it.code][(keys-2).toString()] : 0.00
                                        if(secondVal > 0) {
                                            mapOtherIncome[it.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
                                        }
                                        else {
                                            mapOtherIncome[it.code][pDR.key.toString()] = 0.00 as BigDecimal
                                        }

                                        BigDecimal firstValTotal = mapOtherIncome[(keys-3).toString()] ? mapOtherIncome[(keys-3).toString()] : 0.00
                                        BigDecimal secondValTotal = mapOtherIncome[(keys-2).toString()] ? mapOtherIncome[(keys-2).toString()] : 0.00
                                        if(secondValTotal > 0) {
                                            mapOtherIncome[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
                                        }
                                        else {
                                            mapOtherIncome[pDR.key.toString()] = 0.00
                                        }
                                    }
                                }
                                else {
                                    totalRevenue[pDR.key.toString()] = totalRevenue[pDR.key.toString()] ? totalRevenue[pDR.key.toString()] : 0.00

                                    if (!mapRevenue[it.code]) {
                                        revenue.push(new IncomeStatementAccount(code: it.code, account: it.account))
                                        Map<String,BigDecimal> mapIncomeRev = [:]
                                        mapIncomeRev[pDR.key.toString()] = 0.00
                                        mapRevenue[it.code] = mapIncomeRev as Map<String,BigDecimal>
                                    }

                                    if (compareStart >= 0 && compareEnd <= 0) {
                                        if(!mapRevenue[it.code][pDR.key.toString()]){
                                            mapRevenue[it.code][pDR.key.toString()] = 0.00
                                        }
                                        mapRevenue[it.code][pDR.key.toString()] = mapRevenue[it.code][pDR.key.toString()] + it.amount
                                        totalRevenue[pDR.key.toString()] = totalRevenue[pDR.key.toString()] ? totalRevenue[pDR.key.toString()] : 0.00
                                        totalRevenue[pDR.key.toString()] = totalRevenue[pDR.key.toString()] + it.amount
                                    }

                                    if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapRevenue[it.code][(keys-2).toString()] ? mapRevenue[it.code][(keys-2).toString()] : 0.00
                                        BigDecimal secondVal = mapRevenue[it.code][(keys-1).toString()] ? mapRevenue[it.code][(keys-1).toString()] : 0.00
                                        mapRevenue[it.code][pDR.key.toString()] = firstVal - secondVal

                                        BigDecimal firstValTotal = totalRevenue[(keys-2).toString()] ? totalRevenue[(keys-2).toString()] : 0.00
                                        BigDecimal secondValTotal = totalRevenue[(keys-1).toString()] ? totalRevenue[(keys-1).toString()] : 0.00
                                        totalRevenue[pDR.key.toString()] = firstValTotal - secondValTotal
                                    }

                                    if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapRevenue[it.code][(keys-3).toString()] ? mapRevenue[it.code][(keys-3).toString()] : 0.00
                                        BigDecimal secondVal = mapRevenue[it.code][(keys-2).toString()] ? mapRevenue[it.code][(keys-2).toString()] : 0.00
                                        if(secondVal > 0) {
                                            mapRevenue[it.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
                                        }
                                        else {
                                            mapRevenue[it.code][pDR.key.toString()] = 0.00 as BigDecimal
                                        }

                                        BigDecimal firstValTotal = mapRevenue[(keys-3).toString()] ? mapRevenue[(keys-3).toString()] : 0.00
                                        BigDecimal secondValTotal = mapRevenue[(keys-2).toString()] ? mapRevenue[(keys-2).toString()] : 0.00
                                        if(secondValTotal > 0) {
                                            mapRevenue[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
                                        }
                                        else {
                                            mapRevenue[pDR.key.toString()] = 0.00
                                        }
                                    }
                                }


                        }
                    } else {
                        isPeriodDateRangeChildList.each {
                            pDR ->
                                def compareStart = transDate <=> pDR.start
                                def compareEnd = transDate <=> pDR.end

                                String[] otherInCodes = ['600420']
//                              INTEREST EXPENSE
                                if(Arrays.asList(otherInCodes).contains(it.code)){
                                    totalFinanceCost[pDR.key.toString()] = totalFinanceCost[pDR.key.toString()] ? totalFinanceCost[pDR.key.toString()] : 0.00

                                    if (!mapFinanceCost[it.code]) {
                                        financeCost.push(new IncomeStatementAccount(code: it.code, account: it.account))
                                        Map<String,BigDecimal> mapIncomeFinCost = [:]
                                        mapIncomeFinCost[pDR.key.toString()] = 0.00
                                        mapFinanceCost[it.code] = mapIncomeFinCost as Map<String,BigDecimal>
                                    }

                                    if (compareStart >= 0 && compareEnd <= 0) {
                                        if(!mapFinanceCost[it.code][pDR.key.toString()]){
                                            mapFinanceCost[it.code][pDR.key.toString()] = 0.00
                                        }
                                        mapFinanceCost[it.code][pDR.key.toString()] = mapFinanceCost[it.code][pDR.key.toString()] + it.amount
                                        totalFinanceCost[pDR.key.toString()] = totalFinanceCost[pDR.key.toString()] ? totalFinanceCost[pDR.key.toString()] : 0.00
                                        totalFinanceCost[pDR.key.toString()] = totalFinanceCost[pDR.key.toString()] + it.amount
                                    }

                                    if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapFinanceCost[it.code][(keys-2).toString()] ? mapFinanceCost[it.code][(keys-2).toString()] : 0.00
                                        BigDecimal secondVal = mapFinanceCost[it.code][(keys-1).toString()] ? mapFinanceCost[it.code][(keys-1).toString()] : 0.00
                                        mapFinanceCost[it.code][pDR.key.toString()] = firstVal - secondVal

                                        BigDecimal firstValTotal = totalFinanceCost[(keys-2).toString()] ? totalFinanceCost[(keys-2).toString()] : 0.00
                                        BigDecimal secondValTotal = totalFinanceCost[(keys-1).toString()] ? totalFinanceCost[(keys-1).toString()] : 0.00
                                        totalFinanceCost[pDR.key.toString()] = firstValTotal - secondValTotal
                                    }

                                    if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapFinanceCost[it.code][(keys-3).toString()] ? mapFinanceCost[it.code][(keys-3).toString()] : 0.00
                                        BigDecimal secondVal = mapFinanceCost[it.code][(keys-2).toString()] ? mapFinanceCost[it.code][(keys-2).toString()] : 0.00
                                        if(secondVal > 0) {
                                            mapFinanceCost[it.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
                                        }
                                        else {
                                            mapFinanceCost[it.code][pDR.key.toString()] = 0.00 as BigDecimal
                                        }

                                        BigDecimal firstValTotal = mapFinanceCost[(keys-3).toString()] ? mapFinanceCost[(keys-3).toString()] : 0.00
                                        BigDecimal secondValTotal = mapFinanceCost[(keys-2).toString()] ? mapFinanceCost[(keys-2).toString()] : 0.00
                                        if(secondValTotal > 0) {
                                            mapFinanceCost[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
                                        }
                                        else {
                                            mapFinanceCost[pDR.key.toString()] = 0.00
                                        }
                                    }
                                }
                                else {
                                    totalExpenses[pDR.key.toString()] = totalExpenses[pDR.key.toString()] ? totalExpenses[pDR.key.toString()] : 0.00

                                    if(!mapExpenses[it.code]){
                                        expenses.push(new IncomeStatementAccount(code: it.code,account: it.account))
                                        Map<String,BigDecimal> mapIncomeExp = [:]
                                        mapIncomeExp[pDR.key.toString()] = 0.00
                                        mapExpenses[it.code] = mapIncomeExp  as Map<String,BigDecimal>
                                    }

                                    if(compareStart >= 0 && compareEnd <= 0) {

                                        if(!mapExpenses[it.code][pDR.key.toString()]){
                                            mapExpenses[it.code][pDR.key.toString()] = 0.00
                                        }
                                        mapExpenses[it.code][pDR.key.toString()] = mapExpenses[it.code][pDR.key.toString()] + it.amount

                                        totalExpenses[pDR.key.toString()] = totalExpenses[pDR.key.toString()] + it.amount
                                    }

                                    if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapExpenses[it.code][(keys-2).toString()] ? mapExpenses[it.code][(keys-2).toString()] : 0.00
                                        BigDecimal secondVal = mapExpenses[it.code][(keys-1).toString()] ? mapExpenses[it.code][(keys-1).toString()] : 0.00
                                        mapExpenses[it.code][pDR.key.toString()] = firstVal - secondVal

                                        BigDecimal firstValTotal = totalExpenses[(keys-2).toString()] ? totalExpenses[(keys-2).toString()] : 0.00
                                        BigDecimal secondValTotal = totalExpenses[(keys-1).toString()] ? totalExpenses[(keys-1).toString()] : 0.00
                                        totalExpenses[pDR.key.toString()] = firstValTotal - secondValTotal
                                    }

                                    if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
                                        Integer keys = pDR.key
                                        BigDecimal firstVal = mapExpenses[it.code][(keys-3).toString()] ? mapExpenses[it.code][(keys-3).toString()] : 0.00
                                        BigDecimal secondVal = mapExpenses[it.code][(keys-2).toString()] ? mapExpenses[it.code][(keys-2).toString()] : 0.00
                                        if(secondVal > 0) {
                                            mapExpenses[it.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
                                        }
                                        else {
                                            mapExpenses[it.code][pDR.key.toString()] = 0.00 as BigDecimal
                                        }

                                        BigDecimal firstValTotal = mapExpenses[(keys-3).toString()] ? mapExpenses[(keys-3).toString()] : 0.00
                                        BigDecimal secondValTotal = mapExpenses[(keys-2).toString()] ? mapExpenses[(keys-2).toString()] : 0.00
                                        if(secondValTotal > 0) {
                                            mapExpenses[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
                                        }
                                        else {
                                            mapExpenses[pDR.key.toString()] = 0.00
                                        }
                                    }
                                }
                        }
                    }
            }
        }
        Map<String,BigDecimal> grossProfit = [:]
        Map<String,BigDecimal> grossIncome = [:]
        Map<String,BigDecimal> netOpIncome = [:]
        Map<String,BigDecimal> netProfit = [:]
        Map<String,BigDecimal> netRevenue = [:]

        isPeriodDateRangeChildList.each {
            it ->
                BigDecimal totalCOS = totalCostOfSales[it.key.toString()] ? totalCostOfSales[it.key.toString()] : 0.00
                BigDecimal totalRev = totalRevenue[it.key.toString()] ? totalRevenue[it.key.toString()] : 0.00
                BigDecimal totalExp = totalExpenses[it.key.toString()] ? totalExpenses[it.key.toString()] : 0.00
                BigDecimal totalDisc = totalDiscounts[it.key.toString()] ? totalDiscounts[it.key.toString()] : 0.00
                BigDecimal totalOther = totalOtherIncome[it.key.toString()] ? totalOtherIncome[it.key.toString()] : 0.00
                BigDecimal totalFinCost = totalFinanceCost[it.key.toString()] ? totalFinanceCost[it.key.toString()] : 0.00
                netRevenue[it.key.toString()] = totalRev - totalDisc
                grossProfit[it.key.toString()] = netRevenue[it.key.toString()] - totalCOS
                grossIncome[it.key.toString()] = grossProfit[it.key.toString()] + totalOther
                netOpIncome[it.key.toString()] = grossIncome[it.key.toString()] - totalExp
                netProfit[it.key.toString()] = netOpIncome[it.key.toString()] - totalFinCost
        }

        incomeStatementPage.costOfSales = costOfSales
        incomeStatementPage.revenue = revenue
        incomeStatementPage.expenses = expenses
        incomeStatementPage.discounts = discounts
        incomeStatementPage.otherIncome = otherIncome
        incomeStatementPage.financeCost = financeCost

        incomeStatementPage.mapRevenue = mapRevenue
        incomeStatementPage.mapDiscounts = mapDiscounts
        incomeStatementPage.mapOtherIncome = mapOtherIncome
        incomeStatementPage.mapExpenses = mapExpenses
        incomeStatementPage.mapCostOfSales = mapCostOfSales
        incomeStatementPage.mapFinanceCost = mapFinanceCost
        incomeStatementPage.isPeriodDateRangeChildList = isPeriodDateRangeChildList

        incomeStatementPage.totalRevenue = totalRevenue
        incomeStatementPage.totalDiscounts = totalDiscounts
        incomeStatementPage.totalOtherIncome = totalOtherIncome
        incomeStatementPage.totalCostOfSales = totalCostOfSales
        incomeStatementPage.totalExpenses = totalExpenses
        incomeStatementPage.totalFinanceCost = totalFinanceCost
        incomeStatementPage.grossProfit = grossProfit
        incomeStatementPage.netRevenue = netRevenue
        incomeStatementPage.grossIncome = grossIncome
        incomeStatementPage.netProfit = netProfit
        incomeStatementPage.netOpIncome = netOpIncome

        return incomeStatementPage
    }

    @GraphQLQuery(name="IncomeStatementByFormat")
    List<IncomeStatementFormat> IncomeStatementByFormat(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period,
            @GraphQLArgument(name="format") ArrayList<IncomeStatementFormat> format
    ){
        def formatter = DateTimeFormatter.ofPattern(" d MMMM YYYY")
        LocalDate localStart = LocalDate.parse(start)
        LocalDate localEnd = LocalDate.parse(end)

        ISPeriodRangeParent periodRange = getIncomeStatementPeriodRange(localStart,localEnd,period+1)

        IncomeStatementPage incomeStatementPage = new IncomeStatementPage()

        List<IncomeStatementDto> incomeStatementDtoList = getIncomeStatementPerPeriod(periodRange.start,periodRange.end)

        incomeStatementPage.period = "${localStart.format(formatter)} to ${localEnd.format(formatter)}"

        Map<String,Map<String,BigDecimal>> mapRevenue = [:]

        Map<String,Map<String,BigDecimal>> mapExpenses = [:]

        Map<String,Map<String,BigDecimal>> mapCostOfSales = [:]

        Map<String,Map<String,BigDecimal>> mapAccounts = [:]

        List<IncomeStatementAccount> revenue = new ArrayList<IncomeStatementAccount>()
        List<IncomeStatementAccount> expenses = new ArrayList<IncomeStatementAccount>()
        List<IncomeStatementAccount> costOfSales = new ArrayList<IncomeStatementAccount>()

        Map<String,BigDecimal> totalRevenue = [:]
        Map<String,BigDecimal> totalCostOfSales = [:]
        Map<String,BigDecimal> totalExpenses = [:]

        List<ISPeriodDateRangeChild> isPeriodDateRangeChildList = new ArrayList<ISPeriodDateRangeChild>()
        Integer count = 0
        periodRange.periodDateRangeChildList = periodRange.periodDateRangeChildList.toSorted{ a, b->a.key <=> b.key }
        periodRange.periodDateRangeChildList.each {
            totalRevenue[it.key as String] = 0.00
            totalCostOfSales[it.key as String] = 0.00
            totalExpenses[it.key as String] = 0.00

            ISPeriodDateRangeChild isPeriodDateRangeChild = new ISPeriodDateRangeChild()
            isPeriodDateRangeChild.key = count
            isPeriodDateRangeChild.label = it.label
            isPeriodDateRangeChild.start = it.start
            isPeriodDateRangeChild.end = it.end
            isPeriodDateRangeChildList.push(isPeriodDateRangeChild)
            count++

//            if((count+1)%2){
//                ISPeriodDateRangeChild isPeriodDateRangeChild1 = new ISPeriodDateRangeChild()
//                isPeriodDateRangeChild1.key = count++
//                isPeriodDateRangeChild1.label = 'Balance'
//                isPeriodDateRangeChildList.push(isPeriodDateRangeChild1)
//            }

        }

        isPeriodDateRangeChildList = isPeriodDateRangeChildList.toSorted{ a, b->a.key <=> b.key }

        if(incomeStatementDtoList) {

            (incomeStatementDtoList as List<IncomeStatementDto>).each {
                it ->
                    LocalDate transDate = LocalDate.parse(it.transactionDate)

                    if (it.accountType.equalsIgnoreCase('COST_OF_SALE')) {

                        isPeriodDateRangeChildList.each {
                            pDR ->
                                def compareStart = transDate <=> pDR.start
                                def compareEnd = transDate <=> pDR.end

//                              New Map Integration
                                if (!mapAccounts[it.code]) {
                                    Map<String,BigDecimal> subMapAccounts = [:]
                                    subMapAccounts[pDR.key.toString()] = 0.00
                                    mapAccounts[it.code] = subMapAccounts as Map<String,BigDecimal>
                                }

                                if (compareStart >= 0 && compareEnd <= 0) {
                                    if(!mapAccounts[it.code][pDR.key.toString()]){
                                        mapAccounts[it.code][pDR.key.toString()] = 0.00
                                    }

                                    mapAccounts[it.code][pDR.key.toString()] = mapAccounts[it.code][pDR.key.toString()] + it.amount
                                }

                                if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                    Integer keys = pDR.key
                                    BigDecimal firstVal = mapAccounts[it.code][(keys-2).toString()] ? mapAccounts[it.code][(keys-2).toString()] : 0.00
                                    BigDecimal secondVal = mapAccounts[it.code][(keys-1).toString()] ? mapAccounts[it.code][(keys-1).toString()] : 0.00
                                    mapAccounts[it.code][pDR.key.toString()] = firstVal - secondVal

                                    BigDecimal firstValTotal = totalCostOfSales[(keys-2).toString()] ? totalCostOfSales[(keys-2).toString()] : 0.00
                                    BigDecimal secondValTotal = totalCostOfSales[(keys-1).toString()] ? totalCostOfSales[(keys-1).toString()] : 0.00
                                    totalCostOfSales[pDR.key.toString()] = firstValTotal - secondValTotal
                                }

//                              =====================


                                if (!mapCostOfSales[it.code]) {
                                    costOfSales.push(new IncomeStatementAccount(code: it.code, account: it.account))
                                    Map<String,BigDecimal> mapIncomeCoS = [:]
                                    mapIncomeCoS[pDR.key.toString()] = 0.00
                                    mapCostOfSales[it.code] = mapIncomeCoS as Map<String,BigDecimal>
                                }

                                if (compareStart >= 0 && compareEnd <= 0) {
                                    if(!mapCostOfSales[it.code][pDR.key.toString()]){
                                        mapCostOfSales[it.code][pDR.key.toString()] = 0.00
                                    }
                                    mapCostOfSales[it.code][pDR.key.toString()] = mapCostOfSales[it.code][pDR.key.toString()] + it.amount
                                    totalCostOfSales[pDR.key.toString()] = totalCostOfSales[pDR.key.toString()] ? totalCostOfSales[pDR.key.toString()] : 0.00
                                    totalCostOfSales[pDR.key.toString()] = totalCostOfSales[pDR.key.toString()] + it.amount
                                }

                                if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                    Integer keys = pDR.key
                                    BigDecimal firstVal = mapCostOfSales[it.code][(keys-2).toString()] ? mapCostOfSales[it.code][(keys-2).toString()] : 0.00
                                    BigDecimal secondVal = mapCostOfSales[it.code][(keys-1).toString()] ? mapCostOfSales[it.code][(keys-1).toString()] : 0.00
                                    mapCostOfSales[it.code][pDR.key.toString()] = firstVal - secondVal

                                    BigDecimal firstValTotal = totalCostOfSales[(keys-2).toString()] ? totalCostOfSales[(keys-2).toString()] : 0.00
                                    BigDecimal secondValTotal = totalCostOfSales[(keys-1).toString()] ? totalCostOfSales[(keys-1).toString()] : 0.00
                                    totalCostOfSales[pDR.key.toString()] = firstValTotal - secondValTotal
                                }


                        }
                    } else if (it.accountType.equalsIgnoreCase('REVENUE')) {

                        isPeriodDateRangeChildList.each {
                            pDR ->
                                def compareStart = transDate <=> pDR.start
                                def compareEnd = transDate <=> pDR.end

                                totalRevenue[pDR.key.toString()] = totalRevenue[pDR.key.toString()] ? totalRevenue[pDR.key.toString()] : 0.00

                                if (!mapRevenue[it.code]) {
                                    revenue.push(new IncomeStatementAccount(code: it.code, account: it.account))
                                    Map<String,BigDecimal> mapIncomeRev = [:]
                                    mapIncomeRev[pDR.key.toString()] = 0.00
                                    mapRevenue[it.code] = mapIncomeRev as Map<String,BigDecimal>
                                }

                                if (compareStart >= 0 && compareEnd <= 0) {
                                    if(!mapRevenue[it.code][pDR.key.toString()]){
                                        mapRevenue[it.code][pDR.key.toString()] = 0.00
                                    }
                                    mapRevenue[it.code][pDR.key.toString()] = mapRevenue[it.code][pDR.key.toString()] + it.amount
                                    totalRevenue[pDR.key.toString()] = totalRevenue[pDR.key.toString()] ? totalRevenue[pDR.key.toString()] : 0.00
                                    totalRevenue[pDR.key.toString()] = totalRevenue[pDR.key.toString()] + it.amount
                                }

                                if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                    Integer keys = pDR.key
                                    BigDecimal firstVal = mapRevenue[it.code][(keys-2).toString()] ? mapRevenue[it.code][(keys-2).toString()] : 0.00
                                    BigDecimal secondVal = mapRevenue[it.code][(keys-1).toString()] ? mapRevenue[it.code][(keys-1).toString()] : 0.00
                                    mapRevenue[it.code][pDR.key.toString()] = firstVal - secondVal

                                    BigDecimal firstValTotal = totalRevenue[(keys-2).toString()] ? totalRevenue[(keys-2).toString()] : 0.00
                                    BigDecimal secondValTotal = totalRevenue[(keys-1).toString()] ? totalRevenue[(keys-1).toString()] : 0.00
                                    totalRevenue[pDR.key.toString()] = firstValTotal - secondValTotal
                                }
                        }
                    } else {
                        isPeriodDateRangeChildList.each {
                            pDR ->
                                def compareStart = transDate <=> pDR.start
                                def compareEnd = transDate <=> pDR.end

                                totalExpenses[pDR.key.toString()] = totalExpenses[pDR.key.toString()] ? totalExpenses[pDR.key.toString()] : 0.00

                                if(!mapExpenses[it.code]){
                                    expenses.push(new IncomeStatementAccount(code: it.code,account: it.account))
                                    Map<String,BigDecimal> mapIncomeExp = [:]
                                    mapIncomeExp[pDR.key.toString()] = 0.00
                                    mapExpenses[it.code] = mapIncomeExp  as Map<String,BigDecimal>
                                }

                                if(compareStart >= 0 && compareEnd <= 0) {

                                    if(!mapExpenses[it.code][pDR.key.toString()]){
                                        mapExpenses[it.code][pDR.key.toString()] = 0.00
                                    }
                                    mapExpenses[it.code][pDR.key.toString()] = mapExpenses[it.code][pDR.key.toString()] + it.amount

                                    totalExpenses[pDR.key.toString()] = totalExpenses[pDR.key.toString()] + it.amount
                                }

                                if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                                    Integer keys = pDR.key
                                    BigDecimal firstVal = mapExpenses[it.code][(keys-2).toString()] ? mapExpenses[it.code][(keys-2).toString()] : 0.00
                                    BigDecimal secondVal = mapExpenses[it.code][(keys-1).toString()] ? mapExpenses[it.code][(keys-1).toString()] : 0.00
                                    mapExpenses[it.code][pDR.key.toString()] = firstVal - secondVal

                                    BigDecimal firstValTotal = totalExpenses[(keys-2).toString()] ? totalExpenses[(keys-2).toString()] : 0.00
                                    BigDecimal secondValTotal = totalExpenses[(keys-1).toString()] ? totalExpenses[(keys-1).toString()] : 0.00
                                    totalExpenses[pDR.key.toString()] = firstValTotal - secondValTotal
                                }

                        }
                    }
            }
        }
        Map<String,BigDecimal> grossProfit = [:]
        Map<String,BigDecimal> netProfit = [:]

        isPeriodDateRangeChildList.each {
            it ->
                BigDecimal totalCOS = totalCostOfSales[it.key.toString()] ? totalCostOfSales[it.key.toString()] : 0.00
                BigDecimal totalRev = totalRevenue[it.key.toString()] ? totalRevenue[it.key.toString()] : 0.00
                BigDecimal totalExp = totalExpenses[it.key.toString()] ? totalExpenses[it.key.toString()] : 0.00
                grossProfit[it.key.toString()] = totalRev - totalCOS
                netProfit[it.key.toString()] = grossProfit[it.key.toString()] - totalExp
        }


        incomeStatementPage.costOfSales = costOfSales
        incomeStatementPage.revenue = revenue
        incomeStatementPage.expenses = expenses

        incomeStatementPage.mapRevenue = mapRevenue
        incomeStatementPage.mapExpenses = mapExpenses
        incomeStatementPage.mapCostOfSales = mapCostOfSales
        incomeStatementPage.isPeriodDateRangeChildList = isPeriodDateRangeChildList

        incomeStatementPage.totalRevenue = totalRevenue
        incomeStatementPage.totalCostOfSales = totalCostOfSales
        incomeStatementPage.totalExpenses = totalExpenses
        incomeStatementPage.grossProfit = grossProfit
        incomeStatementPage.netProfit = netProfit


        return incomeStatementPage
    }


//    Balance Sheet
    List<IncomeStatementDto> getBalanceSheetPerPeriod (String start,String end){
        return entityManager.createNativeQuery("""
           select 
                to_char(date("transactionDate" + interval '8 hour'),'YYYY-MM-DD') as "transactionDate",
                "code",
                "mother" as "account",
                "normalSide",
                "accountType",
                sum("amount") as "amount"
            from
                (select 
                "transactionDate",
                "code",
                "mother",
                "normalSide",
                "accountType",
                sum("amount") as "amount"
                from
                    (select 
                        ld.transaction_date as "transactionDate",
                        ld.journal_account->'motherAccount'->>'description' as "mother",
                        ld.journal_account->'motherAccount'->>'code' as "code",
                        ld.journal_account->'motherAccount'->>'normalSide' as "normalSide",
                        cast(ld.journal_account->'subAccount'->>'id' as text) as "subAccountId",
                        ld.journal_account->'subAccount'->>'code' as "subCode",
                        ld.journal_account->'subAccount'->>'description' as "subAccount",
                        cast(ld.journal_account->'subAccount'->>'id' as text) as "subsubAccountId",
                        ld.journal_account->'code' as "subsubCode",
                        ld.journal_account->'description' as "subsubAccount",
                        coalesce(sum(ld.debit),0) as "debit",
                        coalesce(sum(ld.credit),0) as "credit",
                        case 
                            when ld.journal_account->'motherAccount'->>'normalSide' = 'DEBIT' then (coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0)) 
                        else  (coalesce(sum(ld.credit),0)-coalesce(sum(ld.debit),0)) 
                        end as "amount",
                        coa.account_type as "accountType"
                        from accounting.ledger_date ld 
                        left join accounting.header_ledger hl on hl.id  = ld."header"
                        left join accounting.chart_of_accounts coa on coa.account_code = ld.journal_account->'motherAccount'->>'code'
                        where
                        coa.account_type in ('ASSET','LIABILITY','EQUITY') and
                        hl.approved_by is not null
                        and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
                        group  by "mother","subAccount","subsubAccount","subAccountId","subsubAccountId","normalSide","code","subCode","subsubCode", coa.account_type, "transactionDate" 
                        order  by "mother") as subsubaccount
                group by 
                "code",
                "mother",
                "subCode",
                "subAccount",
                "subAccountId",
                "normalSide",
                "accountType",
                "transactionDate"
                order by
                "mother",
                "subAccount") as motheraccount
            group by 
            "code",
            "mother",
            "normalSide",
            "accountType",
            "transactionDate"
            order by "code"
        """)
                .setParameter('start',start)
                .setParameter('end',end)
                .unwrap(NativeQuery.class)
                .setResultTransformer(Transformers.aliasToBean(IncomeStatementDto.class))
                .getResultList();
    }

    @GraphQLQuery(name = "getBalanceSheets")
    BalanceSheetPage getBalanceSheets(
            @GraphQLArgument(name="start") String start,
            @GraphQLArgument(name="end") String end,
            @GraphQLArgument(name="period") Integer period
    ){
        def formatter = DateTimeFormatter.ofPattern(" d MMMM YYYY")
        LocalDate localStart = LocalDate.parse(start)
        LocalDate localEnd = LocalDate.parse(end)

        ISPeriodRangeParent periodRange = getIncomeStatementPeriodRange(localStart,localEnd,period+1)

        BalanceSheetPage balanceSheetPage = new BalanceSheetPage()

        List<IncomeStatementDto> incomeStatementDtoList = getBalanceSheetPerPeriod(periodRange.start,periodRange.end)

        balanceSheetPage.period = "${localStart.format(formatter)} to ${localEnd.format(formatter)}"

        // ASSETS
            // CURRENT ASSETS
            List<IncomeStatementAccount> cashEquiv = new ArrayList<IncomeStatementAccount>()
            List<String> cashEquivAccounts = Arrays.asList('100010','100020','100030','100040','100050','100060')

            List<IncomeStatementAccount> tradeReceivable = new ArrayList<IncomeStatementAccount>()
            List<String> tradeReceivableAccounts = Arrays.asList('100090','100110','100100','100130')

            List<IncomeStatementAccount> allowDbtAcc = new ArrayList<IncomeStatementAccount>()
            List<String> allowDbtAccAccounts = Arrays.asList('100140')

            List<IncomeStatementAccount> advances = new ArrayList<IncomeStatementAccount>()
            List<String> advancesAccounts = Arrays.asList('100140')

            List<IncomeStatementAccount> inventory = new ArrayList<IncomeStatementAccount>()
            List<String> inventoryAccounts = Arrays.asList('100150')

            List<IncomeStatementAccount> toolsSupplies = new ArrayList<IncomeStatementAccount>()
            List<String> toolsSuppliesAccounts = Arrays.asList('100155','100160','100170','100180','100190')

            List<IncomeStatementAccount> inputTax = new ArrayList<IncomeStatementAccount>()
            List<String> inputTaxAccounts = Arrays.asList('100165')

            List<IncomeStatementAccount> prePayments = new ArrayList<IncomeStatementAccount>()
            List<String> prePaymentsAccounts = Arrays.asList('100200','100210', '100220', '100230', '100240')

            List<IncomeStatementAccount> otherCurrAss = new ArrayList<IncomeStatementAccount>()
            List<String> otherCurrAssAccounts = Arrays.asList('100250')

            Map<String,Map<String,BigDecimal>> mapCashEquiv = [:]
            Map<String,Map<String,BigDecimal>> mapTradeReceivable = [:]
            Map<String,Map<String,BigDecimal>> mapAllowDbtAcc = [:]
            Map<String,Map<String,BigDecimal>> mapAdvances = [:]
            Map<String,Map<String,BigDecimal>> mapInventory = [:]
            Map<String,Map<String,BigDecimal>> mapToolsSupplies = [:]
            Map<String,Map<String,BigDecimal>> mapInputTax = [:]
            Map<String,Map<String,BigDecimal>> mapPrePayments = [:]
            Map<String,Map<String,BigDecimal>> mapOtherCurrAss = [:]

            Map<String,BigDecimal> totalCashEquiv = [:]
            Map<String,BigDecimal> totalTradeReceivable = [:]
            Map<String,BigDecimal> totalAllowDbtAcc = [:]
            Map<String,BigDecimal> totalAdvances = [:]
            Map<String,BigDecimal> totalInventory = [:]
            Map<String,BigDecimal> totalToolsSupplies = [:]
            Map<String,BigDecimal> totalInputTax = [:]
            Map<String,BigDecimal> totalPrePayments = [:]
            Map<String,BigDecimal> totalOtherCurrAss = [:]

            // NON CURRENT ASSETS
            List<IncomeStatementAccount> loansReceivable = new ArrayList<IncomeStatementAccount>()
            List<String> loansReceivableAccounts = Arrays.asList('100260')

            List<IncomeStatementAccount> propPlantEquipment = new ArrayList<IncomeStatementAccount>()
            List<String> propPlantEquipmentAccounts = Arrays.asList('100270')

            List<IncomeStatementAccount> accumulatedDep = new ArrayList<IncomeStatementAccount>()
            List<String> accumulatedDepAccounts = Arrays.asList('100290')

            List<IncomeStatementAccount> constructionInProg = new ArrayList<IncomeStatementAccount>()
            List<String> constructionInProgAccounts = Arrays.asList('100300')

            List<IncomeStatementAccount> intangibleAssets = new ArrayList<IncomeStatementAccount>()
            List<String> intangibleAssetsAccounts = Arrays.asList('100280')

            List<IncomeStatementAccount> defferedTaxAssets = new ArrayList<IncomeStatementAccount>()
            List<String> defferedTaxAssetsAccounts = Arrays.asList('100320')

            List<IncomeStatementAccount> otherNonCurrentAssets = new ArrayList<IncomeStatementAccount>()
            List<String> otherNonCurrentAssetsAccounts = Arrays.asList('100310')

            Map<String,Map<String,BigDecimal>> mapLoansReceivable = [:]
            Map<String,Map<String,BigDecimal>> mapPropPlantEquipment = [:]
            Map<String,Map<String,BigDecimal>> mapAccumulatedDep = [:]
            Map<String,Map<String,BigDecimal>> mapConstructionInProg = [:]
            Map<String,Map<String,BigDecimal>> mapIntangibleAssets = [:]
            Map<String,Map<String,BigDecimal>> mapDefferedTaxAssets = [:]
            Map<String,Map<String,BigDecimal>> mapOtherNonCurrentAssets = [:]

            Map<String,BigDecimal> totalLoansReceivable = [:]
            Map<String,BigDecimal> totalPropPlantEquipment = [:]
            Map<String,BigDecimal> totalAccumulatedDep = [:]
            Map<String,BigDecimal> totalConstructionInProg = [:]
            Map<String,BigDecimal> totalIntangibleAssets = [:]
            Map<String,BigDecimal> totalDefferedTaxAssets = [:]
            Map<String,BigDecimal> totalOtherNonCurrentAssets = [:]

        // LIABILITIES
            // CURRENT LIABILITIES
            List<IncomeStatementAccount> dueToDoctors = new ArrayList<IncomeStatementAccount>()
            List<String> dueToDoctorsAccounts = Arrays.asList('200010')
            List<IncomeStatementAccount> accountsPayable = new ArrayList<IncomeStatementAccount>()
            List<String> accountsPayableAccounts = Arrays.asList('200020')
            List<IncomeStatementAccount> nonPayableCurrent = new ArrayList<IncomeStatementAccount>()
            List<String> nonPayableCurrentAccounts = Arrays.asList('200030')
            List<IncomeStatementAccount> accruedInterestPayable = new ArrayList<IncomeStatementAccount>()
            List<String> accruedInterestPayableAccounts = Arrays.asList('200050')
            List<IncomeStatementAccount> accruedSalariesAndWagesPayable = new ArrayList<IncomeStatementAccount>()
            List<String> accruedSalariesAndWagesPayableAccounts = Arrays.asList('200070')
            List<IncomeStatementAccount> accruedVLSPayable = new ArrayList<IncomeStatementAccount>()
            List<String> accruedVLSPayableAccounts = Arrays.asList('200080')
            List<IncomeStatementAccount> benefits = new ArrayList<IncomeStatementAccount>()
            List<String> benefitsAccounts = Arrays.asList('200090','200100','200110','200120','200130')
            List<IncomeStatementAccount> advancesToEmp = new ArrayList<IncomeStatementAccount>()
            List<String> advancesToEmpAccounts = Arrays.asList('200140')
            List<IncomeStatementAccount> taxesPayable = new ArrayList<IncomeStatementAccount>()
            List<String> taxesPayableAccounts = Arrays.asList('200160')
            List<IncomeStatementAccount> permitAndLiscensPayables = new ArrayList<IncomeStatementAccount>()
            List<String> permitAndLiscensPayablesAccounts = Arrays.asList('200170')
            List<IncomeStatementAccount> otherCurrentPayables = new ArrayList<IncomeStatementAccount>()
            List<String> otherCurrentPayablesAccounts = Arrays.asList('200180')

            Map<String,Map<String,BigDecimal>> dueToDoctorsMap  = [:]
            Map<String,Map<String,BigDecimal>> accountsPayableMap  = [:]
            Map<String,Map<String,BigDecimal>> nonPayableCurrentMap  = [:]
            Map<String,Map<String,BigDecimal>> accruedInterestPayableMap  = [:]
            Map<String,Map<String,BigDecimal>> accruedSalariesAndWagesPayableMap  = [:]
            Map<String,Map<String,BigDecimal>> accruedVLSPayableMap  = [:]
            Map<String,Map<String,BigDecimal>> benefitsMap  = [:]
            Map<String,Map<String,BigDecimal>> advancesToEmpMap  = [:]
            Map<String,Map<String,BigDecimal>> taxesPayableMap  = [:]
            Map<String,Map<String,BigDecimal>> permitAndLiscensPayablesMap  = [:]
            Map<String,Map<String,BigDecimal>> otherCurrentPayablesMap  = [:]

            Map<String,BigDecimal> dueToDoctorsTotal  = [:]
            Map<String,BigDecimal> accountsPayableTotal  = [:]
            Map<String,BigDecimal> nonPayableCurrentTotal  = [:]
            Map<String,BigDecimal> accruedInterestPayableTotal  = [:]
            Map<String,BigDecimal> accruedSalariesAndWagesPayableTotal  = [:]
            Map<String,BigDecimal> accruedVLSPayableTotal  = [:]
            Map<String,BigDecimal> benefitsTotal  = [:]
            Map<String,BigDecimal> advancesToEmpTotal  = [:]
            Map<String,BigDecimal> taxesPayableTotal  = [:]
            Map<String,BigDecimal> permitAndLiscensPayablesTotal  = [:]
            Map<String,BigDecimal> otherCurrentPayablesTotal  = [:]
            //NON CURRENT

            List<IncomeStatementAccount> advancesToShareHolders = new ArrayList<IncomeStatementAccount>()
            List<String> advancesToShareHoldersAccounts = Arrays.asList('200195')
            List<IncomeStatementAccount> notesPayableNonCurrent = new ArrayList<IncomeStatementAccount>()
            List<String> notesPayableNonCurrentAccounts = Arrays.asList('200200')
            List<IncomeStatementAccount> otherNonCurrentLiabilities = new ArrayList<IncomeStatementAccount>()
            List<String> otherNonCurrentLiabilitiesAccounts = Arrays.asList('200210','200220')

            Map<String,Map<String,BigDecimal>> advancesToShareHoldersMap  = [:]
            Map<String,Map<String,BigDecimal>> notesPayableNonCurrentMap  = [:]
            Map<String,Map<String,BigDecimal>> otherNonCurrentLiabilitiesMap  = [:]

            Map<String,BigDecimal> advancesToShareHoldersTotal   = [:]
            Map<String,BigDecimal> notesPayableNonCurrentTotal  = [:]
            Map<String,BigDecimal> otherNonCurrentLiabilitiesTotal  = [:]

            //  SHARE HOLDERS EQUITY
            List<IncomeStatementAccount> shareCapital = new ArrayList<IncomeStatementAccount>()
            List<String> shareCapitalAccounts = Arrays.asList('300010')
            List<IncomeStatementAccount> additionalPaidCapital = new ArrayList<IncomeStatementAccount>()
            List<String> additionalPaidCapitalAccounts = Arrays.asList('300020')
            List<IncomeStatementAccount> retainedEarnings = new ArrayList<IncomeStatementAccount>()
            List<String> retainedEarningsAccounts = Arrays.asList('300030')
            List<IncomeStatementAccount> dividends = new ArrayList<IncomeStatementAccount>()
            List<String> dividendsAccounts = Arrays.asList('300050')


            Map<String,Map<String,BigDecimal>> shareCapitalMap = [:]
            Map<String,Map<String,BigDecimal>> additionalPaidCapitalMap = [:]
            Map<String,Map<String,BigDecimal>> retainedEarningsMap = [:]
            Map<String,Map<String,BigDecimal>> dividendsMap = [:]

            Map<String,BigDecimal> shareCapitalTotal = [:]
            Map<String,BigDecimal> additionalPaidCapitalTotal = [:]
            Map<String,BigDecimal> retainedEarningsTotal = [:]
            Map<String,BigDecimal> dividendsTotal = [:]

        List<ISPeriodDateRangeChild> isPeriodDateRangeChildList = new ArrayList<ISPeriodDateRangeChild>()
        Integer count = 0
        periodRange.periodDateRangeChildList = periodRange.periodDateRangeChildList.toSorted{ a, b->a.key <=> b.key }
        periodRange.periodDateRangeChildList.each {
            totalCashEquiv[it.key as String] = 0.00
            totalTradeReceivable[it.key as String] = 0.00
            totalAllowDbtAcc[it.key as String] = 0.00
            totalAdvances[it.key as String] = 0.00
            totalInventory[it.key as String] = 0.00
            totalToolsSupplies[it.key as String] = 0.00

            ISPeriodDateRangeChild isPeriodDateRangeChild = new ISPeriodDateRangeChild()
            isPeriodDateRangeChild.key = count
            isPeriodDateRangeChild.label = it.label
            isPeriodDateRangeChild.start = it.start
            isPeriodDateRangeChild.end = it.end
            isPeriodDateRangeChildList.push(isPeriodDateRangeChild)
            count++

            if((count+1)%2){
                ISPeriodDateRangeChild isPeriodDateRangeChild1 = new ISPeriodDateRangeChild()
                isPeriodDateRangeChild1.key = count++
                isPeriodDateRangeChild1.label = 'Increase (Decrease)'
                isPeriodDateRangeChildList.push(isPeriodDateRangeChild1)

                ISPeriodDateRangeChild isPeriodDateRangeChild2 = new ISPeriodDateRangeChild()
                isPeriodDateRangeChild2.key = count++
                isPeriodDateRangeChild2.label = '%'
                isPeriodDateRangeChildList.push(isPeriodDateRangeChild2)
            }

        }

        isPeriodDateRangeChildList = isPeriodDateRangeChildList.toSorted{ a, b->a.key <=> b.key }

        if(incomeStatementDtoList) {

            (incomeStatementDtoList as List<IncomeStatementDto>).each {
                it ->
                    LocalDate transDate = LocalDate.parse(it.transactionDate)

                    if (it.accountType.equalsIgnoreCase('ASSET')) {
                        // CURRENT ASSETS
                        // CASH EQUIVALENT
                        if (cashEquivAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapCashEquiv, cashEquiv, totalCashEquiv)
                            cashEquiv = mapOAR.iSAccount
                            mapCashEquiv = mapOAR.mapAccounts
                            totalCashEquiv = mapOAR.totalAccount
                        }
                        // Trade Receivable
                        else if (tradeReceivableAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapTradeReceivable, tradeReceivable, totalTradeReceivable)
                            tradeReceivable = mapOAR.iSAccount
                            mapTradeReceivable = mapOAR.mapAccounts
                            totalTradeReceivable = mapOAR.totalAccount
                        }
                        // Allowance
                        else if (allowDbtAccAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapAllowDbtAcc, allowDbtAcc, totalAllowDbtAcc)
                            allowDbtAcc = mapOAR.iSAccount
                            mapAllowDbtAcc = mapOAR.mapAccounts
                            totalAllowDbtAcc = mapOAR.totalAccount
                        }
                        // Advances
                        else if (advancesAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapAdvances, advances, totalAdvances)
                            advances = mapOAR.iSAccount
                            mapAdvances = mapOAR.mapAccounts
                            totalAdvances = mapOAR.totalAccount
                        }
                        // Inventory, end (physical count)
                        else if (inventoryAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapInventory, inventory, totalInventory)
                            inventory = mapOAR.iSAccount
                            mapInventory = mapOAR.mapAccounts
                            totalInventory = mapOAR.totalAccount
                        }
                        // Tools and Supplies
                        else if (toolsSuppliesAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapToolsSupplies, toolsSupplies, totalToolsSupplies)
                            toolsSupplies = mapOAR.iSAccount
                            mapToolsSupplies = mapOAR.mapAccounts
                            totalToolsSupplies = mapOAR.totalAccount
                        }
                        // Input Tax
                        else if (inputTaxAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapInputTax, inputTax, totalInputTax)
                            inputTax = mapOAR.iSAccount
                            mapInputTax = mapOAR.mapAccounts
                            totalInputTax = mapOAR.totalAccount
                        }
                        // Pre payments
                        else if (prePaymentsAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapPrePayments, prePayments, totalPrePayments)
                            prePayments = mapOAR.iSAccount
                            mapPrePayments = mapOAR.mapAccounts
                            totalPrePayments = mapOAR.totalAccount
                        }
                        // Other Current Assets
                        else if (otherCurrAssAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapOtherCurrAss, otherCurrAss, totalOtherCurrAss)
                            otherCurrAss = mapOAR.iSAccount
                            mapOtherCurrAss = mapOAR.mapAccounts
                            totalOtherCurrAss = mapOAR.totalAccount
                        }
                        // NON CURRENT ASSETS
                        // LOANS RECEIVABLES
                        else if (loansReceivableAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapLoansReceivable, loansReceivable, totalLoansReceivable)
                            loansReceivable = mapOAR.iSAccount
                            mapLoansReceivable = mapOAR.mapAccounts
                            totalLoansReceivable = mapOAR.totalAccount
                        }
                        // Property Plan Equipment
                        else if (propPlantEquipmentAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapPropPlantEquipment, propPlantEquipment, totalPropPlantEquipment)
                            propPlantEquipment = mapOAR.iSAccount
                            mapPropPlantEquipment = mapOAR.mapAccounts
                            totalPropPlantEquipment = mapOAR.totalAccount
                        } else if (accumulatedDepAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapAccumulatedDep, accumulatedDep, totalAccumulatedDep)
                            accumulatedDep = mapOAR.iSAccount
                            mapAccumulatedDep = mapOAR.mapAccounts
                            totalAccumulatedDep = mapOAR.totalAccount
                        } else if (constructionInProgAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapConstructionInProg, constructionInProg, totalConstructionInProg)
                            constructionInProg = mapOAR.iSAccount
                            mapConstructionInProg = mapOAR.mapAccounts
                            totalConstructionInProg = mapOAR.totalAccount
                        } else if (intangibleAssetsAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapIntangibleAssets, intangibleAssets, totalIntangibleAssets)
                            intangibleAssets = mapOAR.iSAccount
                            mapIntangibleAssets = mapOAR.mapAccounts
                            totalIntangibleAssets = mapOAR.totalAccount
                        } else if (defferedTaxAssetsAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapDefferedTaxAssets, defferedTaxAssets, totalDefferedTaxAssets)
                            defferedTaxAssets = mapOAR.iSAccount
                            mapDefferedTaxAssets = mapOAR.mapAccounts
                            totalDefferedTaxAssets = mapOAR.totalAccount
                        } else if (otherNonCurrentAssetsAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapOtherNonCurrentAssets, otherNonCurrentAssets, totalOtherNonCurrentAssets)
                            otherNonCurrentAssets = mapOAR.iSAccount
                            mapOtherNonCurrentAssets = mapOAR.mapAccounts
                            totalOtherNonCurrentAssets = mapOAR.totalAccount
                        }
                    }

                    if (it.accountType.equalsIgnoreCase('LIABILITY')) {
                        // LIABILITIES
                        // CURRENT LIABILITIES
                        if (dueToDoctorsAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, dueToDoctorsMap, dueToDoctors, dueToDoctorsTotal)
                            dueToDoctors = mapOAR.iSAccount
                            dueToDoctorsMap = mapOAR.mapAccounts
                            dueToDoctorsTotal = mapOAR.totalAccount
                        } else if (accountsPayableAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accountsPayableMap, accountsPayable, accountsPayableTotal)
                            accountsPayable = mapOAR.iSAccount
                            accountsPayableMap = mapOAR.mapAccounts
                            accountsPayableTotal = mapOAR.totalAccount
                        } else if (nonPayableCurrentAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, nonPayableCurrentMap, nonPayableCurrent, nonPayableCurrentTotal)
                            nonPayableCurrent = mapOAR.iSAccount
                            nonPayableCurrentMap = mapOAR.mapAccounts
                            nonPayableCurrentTotal = mapOAR.totalAccount
                        } else if (accruedInterestPayableAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accruedInterestPayableMap, accruedInterestPayable, accruedInterestPayableTotal)
                            accruedInterestPayable = mapOAR.iSAccount
                            accruedInterestPayableMap = mapOAR.mapAccounts
                            accruedInterestPayableTotal = mapOAR.totalAccount
                        } else if (accruedSalariesAndWagesPayableAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accruedSalariesAndWagesPayableMap, accruedSalariesAndWagesPayable, accruedSalariesAndWagesPayableTotal)
                            accruedSalariesAndWagesPayable = mapOAR.iSAccount
                            accruedSalariesAndWagesPayableMap = mapOAR.mapAccounts
                            accruedSalariesAndWagesPayableTotal = mapOAR.totalAccount
                        } else if (accruedVLSPayableAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accruedVLSPayableMap, accruedVLSPayable, accruedVLSPayableTotal)
                            accruedVLSPayable = mapOAR.iSAccount
                            accruedVLSPayableMap = mapOAR.mapAccounts
                            accruedVLSPayableTotal = mapOAR.totalAccount
                        } else if (benefitsAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, benefitsMap, benefits, benefitsTotal)
                            benefits = mapOAR.iSAccount
                            benefitsMap = mapOAR.mapAccounts
                            benefitsTotal = mapOAR.totalAccount
                        } else if (advancesToEmpAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, advancesToEmpMap, advancesToEmp, advancesToEmpTotal)
                            advancesToEmp = mapOAR.iSAccount
                            advancesToEmpMap = mapOAR.mapAccounts
                            advancesToEmpTotal = mapOAR.totalAccount
                        } else if (taxesPayableAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, taxesPayableMap, taxesPayable, taxesPayableTotal)
                            taxesPayable = mapOAR.iSAccount
                            taxesPayableMap = mapOAR.mapAccounts
                            taxesPayableTotal = mapOAR.totalAccount
                        } else if (permitAndLiscensPayablesAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, permitAndLiscensPayablesMap, permitAndLiscensPayables, permitAndLiscensPayablesTotal)
                            permitAndLiscensPayables = mapOAR.iSAccount
                            permitAndLiscensPayablesMap = mapOAR.mapAccounts
                            permitAndLiscensPayablesTotal = mapOAR.totalAccount
                        } else if (otherCurrentPayablesAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, otherCurrentPayablesMap, otherCurrentPayables, otherCurrentPayablesTotal)
                            otherCurrentPayables = mapOAR.iSAccount
                            otherCurrentPayablesMap = mapOAR.mapAccounts
                            otherCurrentPayablesTotal = mapOAR.totalAccount
                        }
                        // NON CURRENT
                        else if (advancesToShareHoldersAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, advancesToShareHoldersMap, advancesToShareHolders, advancesToShareHoldersTotal)
                            advancesToShareHolders = mapOAR.iSAccount
                            advancesToShareHoldersMap = mapOAR.mapAccounts
                            advancesToShareHoldersTotal = mapOAR.totalAccount
                        } else if (notesPayableNonCurrentAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, notesPayableNonCurrentMap, notesPayableNonCurrent, notesPayableNonCurrentTotal)
                            notesPayableNonCurrent = mapOAR.iSAccount
                            notesPayableNonCurrentMap = mapOAR.mapAccounts
                            notesPayableNonCurrentTotal = mapOAR.totalAccount
                        } else if (otherNonCurrentLiabilitiesAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, otherNonCurrentLiabilitiesMap, otherNonCurrentLiabilities, otherNonCurrentLiabilitiesTotal)
                            otherNonCurrentLiabilities = mapOAR.iSAccount
                            otherNonCurrentLiabilitiesMap = mapOAR.mapAccounts
                            otherNonCurrentLiabilitiesTotal = mapOAR.totalAccount
                        }
                    }

                    if (it.accountType.equalsIgnoreCase('EQUITY')) {
                        if (shareCapitalAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, shareCapitalMap, shareCapital, shareCapitalTotal)
                            shareCapital = mapOAR.iSAccount
                            shareCapitalMap = mapOAR.mapAccounts
                            shareCapitalTotal = mapOAR.totalAccount
                        } else if (additionalPaidCapitalAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, additionalPaidCapitalMap, additionalPaidCapital, additionalPaidCapitalTotal)
                            additionalPaidCapital = mapOAR.iSAccount
                            additionalPaidCapitalMap = mapOAR.mapAccounts
                            additionalPaidCapitalTotal = mapOAR.totalAccount
                        } else if (retainedEarningsAccounts.contains(it.code)) {
                            MapOfAccountsReturn mapOAR =
                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, retainedEarningsMap, retainedEarnings, retainedEarningsTotal)
                            retainedEarnings = mapOAR.iSAccount
                            retainedEarningsMap = mapOAR.mapAccounts
                            retainedEarningsTotal = mapOAR.totalAccount
                        }
                    }
            }

        }




        Map<String,BigDecimal> assets = [:]
        Map<String,BigDecimal> liabilities = [:]
        Map<String,BigDecimal> equity = [:]
        Map<String,BigDecimal> totalLiabilitiesAndEquity = [:]
        Map<String,BigDecimal> totalCurrentAssets = [:]
        Map<String,BigDecimal> totalNonCurrentAssets = [:]
        Map<String,BigDecimal> totalCurrentLiabilities = [:]
        Map<String,BigDecimal> totalNonCurrentLiabilities = [:]
        Map<String,BigDecimal> totalEquity = [:]

        isPeriodDateRangeChildList.each {
            it ->
                // Current Assets
                BigDecimal totalCE = totalCashEquiv[it.key.toString()] ? totalCashEquiv[it.key.toString()] : 0.00
                BigDecimal totalTR = totalTradeReceivable[it.key.toString()] ? totalTradeReceivable[it.key.toString()] : 0.00
                BigDecimal totalADA = totalAllowDbtAcc[it.key.toString()] ? totalAllowDbtAcc[it.key.toString()] : 0.00
                BigDecimal totalAdv = totalAdvances[it.key.toString()] ? totalAdvances[it.key.toString()] : 0.00
                BigDecimal totalInv = totalInventory[it.key.toString()] ? totalInventory[it.key.toString()] : 0.00
                BigDecimal totalTS = totalToolsSupplies[it.key.toString()] ? totalToolsSupplies[it.key.toString()] : 0.00
                BigDecimal totalIT = totalInputTax[it.key.toString()] ? totalInputTax[it.key.toString()] : 0.00
                BigDecimal totalPP = totalPrePayments[it.key.toString()] ? totalPrePayments[it.key.toString()] : 0.00
                BigDecimal totalOCA = totalOtherCurrAss[it.key.toString()] ? totalOtherCurrAss[it.key.toString()] : 0.00
                totalCurrentAssets[it.key.toString()] =
                        totalCE + totalTR + totalAdv + totalInv + totalTS + totalIT + totalPP + totalOCA - totalADA

                BigDecimal totalNonLR = totalLoansReceivable[it.key.toString()] ? totalLoansReceivable[it.key.toString()] : 0.00
                BigDecimal totalNonPPE = totalPropPlantEquipment[it.key.toString()] ? totalPropPlantEquipment[it.key.toString()] : 0.00
                BigDecimal totalNonACD = totalAccumulatedDep[it.key.toString()] ? totalAccumulatedDep[it.key.toString()] : 0.00
                BigDecimal totalNonCIP = totalConstructionInProg[it.key.toString()] ? totalConstructionInProg[it.key.toString()] : 0.00
                BigDecimal totalNonITA = totalIntangibleAssets[it.key.toString()] ? totalIntangibleAssets[it.key.toString()] : 0.00
                BigDecimal totalNonDTA = totalDefferedTaxAssets[it.key.toString()] ? totalDefferedTaxAssets[it.key.toString()] : 0.00
                BigDecimal totalNonONC = totalOtherNonCurrentAssets[it.key.toString()] ? totalOtherNonCurrentAssets[it.key.toString()] : 0.00
                totalNonCurrentAssets[it.key.toString()] =
                        totalNonLR + totalNonPPE + totalNonCIP + totalNonITA + totalNonDTA + totalNonONC - totalNonACD

                assets[it.key.toString()] = totalCurrentAssets[it.key.toString()] + totalNonCurrentAssets[it.key.toString()]

                //LIABILITIES
                //CURRENT ASSETS
                BigDecimal totalLIADTD =  dueToDoctorsTotal[it.key.toString()] ? dueToDoctorsTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAAP =  accountsPayableTotal[it.key.toString()] ? accountsPayableTotal[it.key.toString()] : 0.00
                BigDecimal totalLIANPC =  nonPayableCurrentTotal[it.key.toString()] ? nonPayableCurrentTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAAIP =  accruedInterestPayableTotal[it.key.toString()] ? accruedInterestPayableTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAASWP =  accruedSalariesAndWagesPayableTotal[it.key.toString()] ? accruedSalariesAndWagesPayableTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAVSP =  accruedVLSPayableTotal[it.key.toString()] ? accruedVLSPayableTotal[it.key.toString()] : 0.00
                BigDecimal totalLIABT =  benefitsTotal[it.key.toString()] ? benefitsTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAATET =  advancesToEmpTotal[it.key.toString()] ? advancesToEmpTotal[it.key.toString()] : 0.00
                BigDecimal totalLIATPT =  taxesPayableTotal[it.key.toString()] ? taxesPayableTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAPAL =  permitAndLiscensPayablesTotal[it.key.toString()] ? permitAndLiscensPayablesTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAOCP =  otherCurrentPayablesTotal[it.key.toString()] ? otherCurrentPayablesTotal[it.key.toString()] : 0.00
                totalCurrentLiabilities[it.key.toString()] =
                        totalLIADTD + totalLIAAP + totalLIANPC + totalLIAAIP + totalLIAASWP + totalLIABT + totalLIAATET + totalLIATPT + totalLIAPAL + totalLIAOCP + totalLIAVSP
                // NON CURRENT
                BigDecimal totalLIAATS =  advancesToShareHoldersTotal[it.key.toString()] ? advancesToShareHoldersTotal[it.key.toString()] : 0.00
                BigDecimal totalLIANPNC =  notesPayableNonCurrentTotal[it.key.toString()] ? notesPayableNonCurrentTotal[it.key.toString()] : 0.00
                BigDecimal totalLIAONCLT =  otherNonCurrentLiabilitiesTotal[it.key.toString()] ? otherNonCurrentLiabilitiesTotal[it.key.toString()] : 0.00
                totalNonCurrentLiabilities[it.key.toString()] = totalLIAATS + totalLIANPNC + totalLIAONCLT

                liabilities[it.key.toString()] = totalCurrentLiabilities[it.key.toString()] + totalNonCurrentLiabilities[it.key.toString()]

                //    EQUITY
                 BigDecimal totalSCT = shareCapitalTotal[it.key.toString()] ? shareCapitalTotal[it.key.toString()] : 0.00
                 BigDecimal totalAPCT = additionalPaidCapitalTotal[it.key.toString()] ? additionalPaidCapitalTotal[it.key.toString()] : 0.00
                BigDecimal totalRET = retainedEarningsTotal[it.key.toString()] ? retainedEarningsTotal[it.key.toString()] : 0.00
                BigDecimal totalDEV = dividendsTotal[it.key.toString()] ? dividendsTotal[it.key.toString()] : 0.00

                equity[it.key.toString()] = totalSCT + totalAPCT + totalRET - totalDEV

                totalLiabilitiesAndEquity[it.key.toString()] = liabilities[it.key.toString()] + equity[it.key.toString()]
        }




        balanceSheetPage.assets = assets
        balanceSheetPage.liabilities = liabilities
        balanceSheetPage.equity = equity
        balanceSheetPage.totalLiabilitiesAndEquity = totalLiabilitiesAndEquity

        balanceSheetPage.totalCurrentAssets = totalCurrentAssets
        balanceSheetPage.totalNonCurrentAssets = totalNonCurrentAssets
        balanceSheetPage.totalCurrentLiabilities = totalCurrentLiabilities
        balanceSheetPage.totalNonCurrentLiabilities = totalNonCurrentLiabilities


        balanceSheetPage.isPeriodDateRangeChildList = isPeriodDateRangeChildList
        /* Assets */
            // CURRENT ASSETS
            balanceSheetPage.cashEquiv = cashEquiv
            balanceSheetPage.mapCashEquiv = mapCashEquiv
            balanceSheetPage.totalCashEquiv = totalCashEquiv

            balanceSheetPage.tradeReceivable = tradeReceivable
            balanceSheetPage.mapTradeReceivable = mapTradeReceivable
            balanceSheetPage.totalTradeReceivable = totalTradeReceivable

            balanceSheetPage.allowDbtAcc = allowDbtAcc
            balanceSheetPage.mapAllowDbtAcc = mapAllowDbtAcc
            balanceSheetPage.totalAllowDbtAcc = totalAllowDbtAcc

            balanceSheetPage.advances = advances
            balanceSheetPage.mapAdvances = mapAdvances
            balanceSheetPage.totalAdvances = totalAdvances

            balanceSheetPage.inventory = inventory
            balanceSheetPage.mapInventory = mapInventory
            balanceSheetPage.totalInventory = totalInventory

            balanceSheetPage.toolsSupplies = toolsSupplies
            balanceSheetPage.mapToolsSupplies = mapToolsSupplies
            balanceSheetPage.totalToolsSupplies = totalToolsSupplies

            balanceSheetPage.inputTax = inputTax
            balanceSheetPage.mapInputTax = mapInputTax
            balanceSheetPage.totalInputTax = totalInputTax

            balanceSheetPage.prePayments = prePayments
            balanceSheetPage.mapPrePayments = mapPrePayments
            balanceSheetPage.totalPrePayments = totalPrePayments

            balanceSheetPage.otherCurrAss = otherCurrAss
            balanceSheetPage.mapOtherCurrAss = mapOtherCurrAss
            balanceSheetPage.totalOtherCurrAss = totalOtherCurrAss

            // NON CURRENT ASSETS
            balanceSheetPage.loansReceivable = loansReceivable
            balanceSheetPage.propPlantEquipment = propPlantEquipment
            balanceSheetPage.accumulatedDep = accumulatedDep
            balanceSheetPage.constructionInProg = constructionInProg
            balanceSheetPage.intangibleAssets = intangibleAssets
            balanceSheetPage.defferedTaxAssets = defferedTaxAssets
            balanceSheetPage.otherNonCurrentAssets = otherNonCurrentAssets

            balanceSheetPage.mapLoansReceivable = mapLoansReceivable
            balanceSheetPage.mapPropPlantEquipment = mapPropPlantEquipment
            balanceSheetPage.mapAccumulatedDep = mapAccumulatedDep
            balanceSheetPage.mapConstructionInProg = mapConstructionInProg
            balanceSheetPage.mapIntangibleAssets = mapIntangibleAssets
            balanceSheetPage.mapDefferedTaxAssets = mapDefferedTaxAssets
            balanceSheetPage.mapOtherNonCurrentAssets = mapOtherNonCurrentAssets

            balanceSheetPage.totalLoansReceivable = totalLoansReceivable
            balanceSheetPage.totalPropPlantEquipment = totalPropPlantEquipment
            balanceSheetPage.totalAccumulatedDep = totalAccumulatedDep
            balanceSheetPage.totalConstructionInProg = totalConstructionInProg
            balanceSheetPage.totalIntangibleAssets = totalIntangibleAssets
            balanceSheetPage.totalDefferedTaxAssets = totalDefferedTaxAssets
            balanceSheetPage.totalOtherNonCurrentAssets = totalOtherNonCurrentAssets

            //LIABILITIES
            //CURRENT ASSETS
            balanceSheetPage.dueToDoctors = dueToDoctors
            balanceSheetPage.accountsPayable = accountsPayable
            balanceSheetPage.nonPayableCurrent = nonPayableCurrent
            balanceSheetPage.accruedInterestPayable = accruedInterestPayable
            balanceSheetPage.accruedSalariesAndWagesPayable = accruedSalariesAndWagesPayable
            balanceSheetPage.accruedVLSPayable = accruedVLSPayable
            balanceSheetPage.benefits = benefits
            balanceSheetPage.advancesToEmp = advancesToEmp
            balanceSheetPage.taxesPayable = taxesPayable
            balanceSheetPage.permitAndLiscensPayables = permitAndLiscensPayables
            balanceSheetPage.otherCurrentPayables = otherCurrentPayables

            balanceSheetPage.dueToDoctorsMap = dueToDoctorsMap
            balanceSheetPage.accountsPayableMap = accountsPayableMap
            balanceSheetPage.nonPayableCurrentMap = nonPayableCurrentMap
            balanceSheetPage.accruedInterestPayableMap = accruedInterestPayableMap
            balanceSheetPage.accruedSalariesAndWagesPayableMap = accruedSalariesAndWagesPayableMap
            balanceSheetPage.accruedVLSPayableMap = accruedVLSPayableMap
            balanceSheetPage.benefitsMap = benefitsMap
            balanceSheetPage.advancesToEmpMap = advancesToEmpMap
            balanceSheetPage.taxesPayableMap = taxesPayableMap
            balanceSheetPage.permitAndLiscensPayablesMap = permitAndLiscensPayablesMap
            balanceSheetPage.otherCurrentPayablesMap = otherCurrentPayablesMap

            balanceSheetPage.dueToDoctorsTotal = dueToDoctorsTotal
            balanceSheetPage.accountsPayableTotal = accountsPayableTotal
            balanceSheetPage.nonPayableCurrentTotal = nonPayableCurrentTotal
            balanceSheetPage.accruedInterestPayableTotal = accruedInterestPayableTotal
            balanceSheetPage.accruedSalariesAndWagesPayableTotal = accruedSalariesAndWagesPayableTotal
            balanceSheetPage.accruedVLSPayableTotal = accruedVLSPayableTotal
            balanceSheetPage.benefitsTotal = benefitsTotal
            balanceSheetPage.advancesToEmpTotal = advancesToEmpTotal
            balanceSheetPage.taxesPayableTotal = taxesPayableTotal
            balanceSheetPage.permitAndLiscensPayablesTotal = permitAndLiscensPayablesTotal
            balanceSheetPage.otherCurrentPayablesTotal = otherCurrentPayablesTotal
            // NON CURRENT LIABILITIES
            balanceSheetPage.advancesToShareHolders = advancesToShareHolders
            balanceSheetPage.notesPayableNonCurrent = notesPayableNonCurrent
            balanceSheetPage.otherNonCurrentLiabilities = otherNonCurrentLiabilities

            balanceSheetPage.advancesToShareHoldersMap = advancesToShareHoldersMap
            balanceSheetPage.notesPayableNonCurrentMap = notesPayableNonCurrentMap
            balanceSheetPage.otherNonCurrentLiabilitiesMap = otherNonCurrentLiabilitiesMap

            balanceSheetPage.advancesToShareHoldersTotal = advancesToShareHoldersTotal
            balanceSheetPage.notesPayableNonCurrentTotal = notesPayableNonCurrentTotal
            balanceSheetPage.otherNonCurrentLiabilitiesTotal = otherNonCurrentLiabilitiesTotal

            balanceSheetPage.shareCapital = shareCapital
            balanceSheetPage.additionalPaidCapital = additionalPaidCapital
            balanceSheetPage.retainedEarnings = retainedEarnings
            balanceSheetPage.dividends = dividends

            balanceSheetPage.shareCapitalMap = shareCapitalMap
            balanceSheetPage.additionalPaidCapitalMap = additionalPaidCapitalMap
            balanceSheetPage.retainedEarningsMap = retainedEarningsMap
            balanceSheetPage.dividendsMap = dividendsMap

            balanceSheetPage.shareCapitalTotal = shareCapitalTotal
            balanceSheetPage.additionalPaidCapitalTotal = additionalPaidCapitalTotal
            balanceSheetPage.retainedEarningsTotal = retainedEarningsTotal
            balanceSheetPage.dividendsTotal = dividendsTotal

        return balanceSheetPage
    }

    static MapOfAccountsReturn mapOfAccounts(
            List<ISPeriodDateRangeChild> isPeriodDateRangeChildList,
            LocalDate transDate,
            IncomeStatementDto iSDto,
            Map<String,Map<String,BigDecimal>> mapAccounts,
            List<IncomeStatementAccount> iSAccount,
            Map<String,BigDecimal> totalAccount
    ){
        MapOfAccountsReturn mapOAR = new MapOfAccountsReturn()
        mapOAR.mapAccounts = mapAccounts
        mapOAR.iSAccount = iSAccount
        mapOAR.totalAccount = totalAccount
        isPeriodDateRangeChildList.each {
            pDR ->
                def compareStart = transDate <=> pDR.start
                def compareEnd = transDate <=> pDR.end

                if (! mapOAR.mapAccounts[iSDto.code]) {
                    mapOAR.iSAccount.push(new IncomeStatementAccount(code: iSDto.code, account: iSDto.account))
                    Map<String,BigDecimal> mapISAccount = [:]
                    mapISAccount[pDR.key.toString()] = 0.00
                    mapOAR.mapAccounts[iSDto.code] = mapISAccount as Map<String,BigDecimal>
                }

                if (compareStart >= 0 && compareEnd <= 0) {
                    if(!mapOAR.mapAccounts[iSDto.code][pDR.key.toString()]){
                        mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = 0.00
                    }
                    mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] + iSDto.amount
                    mapOAR.totalAccount[pDR.key.toString()] = mapOAR.totalAccount[pDR.key.toString()] ? mapOAR.totalAccount[pDR.key.toString()] : 0.00
                    mapOAR.totalAccount[pDR.key.toString()] = mapOAR.totalAccount[pDR.key.toString()] + iSDto.amount
                }

//                                For comparison every 2 consecutive dates
                if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
                    Integer keys = pDR.key
                    BigDecimal firstVal = mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] : 0.00
                    BigDecimal secondVal = mapOAR.mapAccounts[iSDto.code][(keys-1).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-1).toString()] : 0.00
                    mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = firstVal - secondVal

                    BigDecimal firstValTotal = mapOAR.totalAccount[(keys-2).toString()] ? mapOAR.totalAccount[(keys-2).toString()] : 0.00
                    BigDecimal secondValTotal = mapOAR.totalAccount[(keys-1).toString()] ? mapOAR.totalAccount[(keys-1).toString()] : 0.00
                    mapOAR.totalAccount[pDR.key.toString()] = firstValTotal - secondValTotal
                }

                if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
                    Integer keys = pDR.key
                    BigDecimal firstVal = mapOAR.mapAccounts[iSDto.code][(keys-3).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-3).toString()] : 0.00
                    BigDecimal secondVal = mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] : 0.00
                    if(secondVal > 0) {
                        mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
                    }
                    else {
                        mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = 0.00 as BigDecimal
                    }

                    BigDecimal firstValTotal = mapOAR.totalAccount[(keys-3).toString()] ? mapOAR.totalAccount[(keys-3).toString()] : 0.00
                    BigDecimal secondValTotal = mapOAR.totalAccount[(keys-2).toString()] ? mapOAR.totalAccount[(keys-2).toString()] : 0.00
                    if(secondValTotal > 0) {
                        mapOAR.totalAccount[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
                    }
                    else {
                        mapOAR.totalAccount[pDR.key.toString()] = 0.00
                    }
                }
        }

        return mapOAR
    }
//
//    @GraphQLQuery(name="getISNetProfit")
//    BalanceSheetSavedAmounts getISNetProfit(
//            @GraphQLArgument(name="year") Integer year
//    ){
//        return onProcessedISPerYear(year)
//    }
//
//    BalanceSheetSavedAmounts onRetainedEarnings(String year){
//        Integer yearInt = year as Integer
//        Integer lastYear = LocalDate.now().withYear(yearInt).minusYears(1).getYear()
//        Integer currentYear = LocalDate.now().withYear(yearInt).getYear()
//        BigDecimal retainEnding = 0.00
//        def bsSavedAmt = balanceSheetsSavedAmountsService.getBSheetsAmountsByYear(year)
//        if(bsSavedAmt.retained_earnings == 0){
//            if(year.equalsIgnoreCase('2020')){
//                bsSavedAmt.retained_earnings = onProcessedISPerYear(yearInt).netProfit
//            }
//            if(yearInt > 2020){
//                BalanceSheetSavedAmounts lastYearRetain = onProcessedISPerYear(lastYear)
//                if(lastYearRetain.retained_earnings == 0){
//                    onRetainedEarnings(lastYear)
//                }
//                else {
//                    bsSavedAmt.retained_earnings = onProcessedISPerYear(yearInt)
//                }
//
//            }
//        }
//
//
//
//    }
//
//
//    BalanceSheetSavedAmounts onProcessedISPerYear(
//            Integer year
//    ){
//        LocalDate localStart = LocalDate.now().withYear(year).withMonth(1).with(TemporalAdjusters.firstDayOfMonth())
//        LocalDate localEnd = LocalDate.now().withYear(year).withMonth(12).with(TemporalAdjusters.lastDayOfMonth())
//
//        def bsSavedAmt = balanceSheetsSavedAmountsService.getBSheetsAmountsByYear(localStart.getYear().toString())
//        if(bsSavedAmt.id){
//            return  bsSavedAmt
//        }
//        else {
//            List<IncomeStatementDto> incomeStatementDtoList = getIncomeStatementPerPeriod(localStart.toString(),localEnd.toString())
//            BigDecimal totalRevenue = 0.00
//            BigDecimal totalCostOfSales = 0.00
//            BigDecimal totalExpenses = 0.00
//            BigDecimal totalDiscounts = 0.00
//            BigDecimal totalOtherIncome = 0.00
//            BigDecimal totalFinanceCost = 0.00
//
//            if(incomeStatementDtoList) {
//
//                (incomeStatementDtoList as List<IncomeStatementDto>).each {
//                    it ->
//                        LocalDate transDate = LocalDate.parse(it.transactionDate)
//
//                        if (it.accountType.equalsIgnoreCase('COST_OF_SALE')) {
//                            totalCostOfSales = totalCostOfSales + it.amount
//                        } else if (it.accountType.equalsIgnoreCase('REVENUE')) {
//                            String[] otherInCodes = ['400060','400070','400090']
//    //                                DISCOUNT AND ALLOWANCE
//                            if(it.code.equalsIgnoreCase('400080')){
//                                totalDiscounts = totalDiscounts + it.amount
//                            }
//                            else if(Arrays.asList(otherInCodes).contains(it.code)){
//                                totalOtherIncome = totalOtherIncome + it.amount
//                            }
//                            else {
//                                totalRevenue = totalRevenue + it.amount
//                            }
//                        } else {
//                            String[] otherInCodes = ['600420']
//    //                              INTEREST EXPENSE
//                            if(Arrays.asList(otherInCodes).contains(it.code)){
//                                totalFinanceCost = totalFinanceCost + it.amount
//                            }
//                            else {
//                                totalExpenses = totalExpenses + it.amount
//                            }
//                        }
//                }
//            }
//            BigDecimal grossProfit = 0.00
//            BigDecimal grossIncome = 0.00
//            BigDecimal netOpIncome = 0.00
//            BigDecimal netProfit = 0.00
//            BigDecimal netRevenue = 0.00
//
//            netRevenue = totalRevenue - totalDiscounts
//            grossProfit = netRevenue - totalCostOfSales
//            grossIncome = grossProfit + totalOtherIncome
//            netOpIncome = grossIncome - totalExpenses
//            netProfit = netOpIncome - totalFinanceCost
//
//            BalanceSheetSavedAmounts balanceSheetSavedAmounts = new BalanceSheetSavedAmounts()
//            balanceSheetSavedAmounts.netProfit = netProfit
//            balanceSheetSavedAmounts.year = year.toString()
//            balanceSheetsSavedAmountsService.save(balanceSheetSavedAmounts)
//            return balanceSheetSavedAmounts
//        }
//
//    }

}
