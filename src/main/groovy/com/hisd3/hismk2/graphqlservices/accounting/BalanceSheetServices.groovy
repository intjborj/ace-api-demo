package com.hisd3.hismk2.graphqlservices.accounting

import groovy.transform.Canonical
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.hibernate.query.NativeQuery
import org.hibernate.transform.Transformers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import javax.persistence.EntityManager
import javax.transaction.Transactional
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.time.temporal.TemporalAdjusters
//
//
//@Canonical
//class  BSPeriodRangeParent {
//    List<BSPeriodDateRangeChild> periodDateRangeChildList
//    String start
//    String end
//}
//
//
//@Canonical
//class  BSPeriodDateRangeChild {
//    Integer key
//    String label
//    LocalDate start
//    LocalDate end
//}
//
//
//@Canonical
//class BalanceSheetAccount {
//    String code
//    String account
//}
//
//
//@Canonical
//class BalanceSheetPage {
//    // Assets
//    // Current Assets
//    List<BalanceSheetAccount> cashEquiv
//    List<BalanceSheetAccount> tradeReceivable
//    List<BalanceSheetAccount> allowDbtAcc
//    List<BalanceSheetAccount> advances
//    List<BalanceSheetAccount> inventory
//    List<BalanceSheetAccount> toolsSupplies
//    List<BalanceSheetAccount> inputTax
//    List<BalanceSheetAccount> prePayments
//    List<BalanceSheetAccount> otherCurrAss
//    // Non Current Assets
//    List<BalanceSheetAccount> loansReceivable
//    List<BalanceSheetAccount> propPlantEquipment
//    List<BalanceSheetAccount> accumulatedDep
//    List<BalanceSheetAccount> constructionInProg
//    List<BalanceSheetAccount> intangibleAssets
//    List<BalanceSheetAccount> defferedTaxAssets
//    List<BalanceSheetAccount> otherNonCurrentAssets
//
//    // Map Assets
//    // Current Assets
//    Map<String,Map<String,BigDecimal>> mapCashEquiv
//    Map<String,Map<String,BigDecimal>> mapTradeReceivable
//    Map<String,Map<String,BigDecimal>> mapAllowDbtAcc
//    Map<String,Map<String,BigDecimal>> mapAdvances
//    Map<String,Map<String,BigDecimal>> mapInventory
//    Map<String,Map<String,BigDecimal>> mapToolsSupplies
//    Map<String,Map<String,BigDecimal>> mapInputTax
//    Map<String,Map<String,BigDecimal>> mapPrePayments
//    Map<String,Map<String,BigDecimal>> mapOtherCurrAss
//    // Non Current Assets
//    Map<String,Map<String,BigDecimal>> mapLoansReceivable
//    Map<String,Map<String,BigDecimal>> mapPropPlantEquipment
//    Map<String,Map<String,BigDecimal>> mapAccumulatedDep
//    Map<String,Map<String,BigDecimal>> mapConstructionInProg
//    Map<String,Map<String,BigDecimal>> mapIntangibleAssets
//    Map<String,Map<String,BigDecimal>> mapDefferedTaxAssets
//    Map<String,Map<String,BigDecimal>> mapOtherNonCurrentAssets
//
//    // Total of Assets
//    // Current Assets
//    Map<String,BigDecimal> totalCashEquiv
//    Map<String,BigDecimal> totalTradeReceivable
//    Map<String,BigDecimal> totalAllowDbtAcc
//    Map<String,BigDecimal> totalAdvances
//    Map<String,BigDecimal> totalInventory
//    Map<String,BigDecimal> totalToolsSupplies
//    Map<String,BigDecimal> totalInputTax
//    Map<String,BigDecimal> totalPrePayments
//    Map<String,BigDecimal> totalOtherCurrAss
//    // Non Current Assets
//    Map<String,BigDecimal> totalLoansReceivable
//    Map<String,BigDecimal> totalPropPlantEquipment
//    Map<String,BigDecimal> totalAccumulatedDep
//    Map<String,BigDecimal> totalConstructionInProg
//    Map<String,BigDecimal> totalIntangibleAssets
//    Map<String,BigDecimal> totalDefferedTaxAssets
//    Map<String,BigDecimal> totalOtherNonCurrentAssets
//
//    // LIABILITIES
//    // CURRENT LIABILITIES
//    List<BalanceSheetAccount> dueToDoctors
//    List<BalanceSheetAccount> accountsPayable
//    List<BalanceSheetAccount> nonPayableCurrent
//    List<BalanceSheetAccount> accruedInterestPayable
//    List<BalanceSheetAccount> accruedSalariesAndWagesPayable
//    List<BalanceSheetAccount> accruedVLSPayable
//    List<BalanceSheetAccount> benefits
//    List<BalanceSheetAccount> advancesToEmp
//    List<BalanceSheetAccount> taxesPayable
//    List<BalanceSheetAccount> permitAndLiscensPayables
//    List<BalanceSheetAccount> otherCurrentPayables
//
//    Map<String,Map<String,BigDecimal>> dueToDoctorsMap
//    Map<String,Map<String,BigDecimal>> accountsPayableMap
//    Map<String,Map<String,BigDecimal>> nonPayableCurrentMap
//    Map<String,Map<String,BigDecimal>> accruedInterestPayableMap
//    Map<String,Map<String,BigDecimal>> accruedSalariesAndWagesPayableMap
//    Map<String,Map<String,BigDecimal>> accruedVLSPayableMap
//    Map<String,Map<String,BigDecimal>> benefitsMap
//    Map<String,Map<String,BigDecimal>> advancesToEmpMap
//    Map<String,Map<String,BigDecimal>> taxesPayableMap
//    Map<String,Map<String,BigDecimal>> permitAndLiscensPayablesMap
//    Map<String,Map<String,BigDecimal>> otherCurrentPayablesMap
//
//    Map<String,BigDecimal> dueToDoctorsTotal
//    Map<String,BigDecimal> accountsPayableTotal
//    Map<String,BigDecimal> nonPayableCurrentTotal
//    Map<String,BigDecimal> accruedInterestPayableTotal
//    Map<String,BigDecimal> accruedSalariesAndWagesPayableTotal
//    Map<String,BigDecimal> accruedVLSPayableTotal
//    Map<String,BigDecimal> benefitsTotal
//    Map<String,BigDecimal> advancesToEmpTotal
//    Map<String,BigDecimal> taxesPayableTotal
//    Map<String,BigDecimal> permitAndLiscensPayablesTotal
//    Map<String,BigDecimal> otherCurrentPayablesTotal
//
//    //NON CURRENT LIABILITIES
//    List<BalanceSheetAccount> advancesToShareHolders
//    List<BalanceSheetAccount> notesPayableNonCurrent
//    List<BalanceSheetAccount> otherNonCurrentLiabilities
//
//    Map<String,Map<String,BigDecimal>> advancesToShareHoldersMap
//    Map<String,Map<String,BigDecimal>> notesPayableNonCurrentMap
//    Map<String,Map<String,BigDecimal>> otherNonCurrentLiabilitiesMap
//
//    Map<String,BigDecimal> advancesToShareHoldersTotal
//    Map<String,BigDecimal> notesPayableNonCurrentTotal
//    Map<String,BigDecimal> otherNonCurrentLiabilitiesTotal
//
//    // SHARE HOLDER EQUITY
//    List<BalanceSheetAccount> shareCapital
//    List<BalanceSheetAccount> additionalPaidCapital
//    List<BalanceSheetAccount> retainedEarnings
//    List<BalanceSheetAccount> dividends
//
//    Map<String,Map<String,BigDecimal>> shareCapitalMap
//    Map<String,Map<String,BigDecimal>> additionalPaidCapitalMap
//    Map<String,Map<String,BigDecimal>> retainedEarningsMap
//    Map<String,Map<String,BigDecimal>> dividendsMap
//
//    Map<String,BigDecimal> shareCapitalTotal
//    Map<String,BigDecimal> additionalPaidCapitalTotal
//    Map<String,BigDecimal> retainedEarningsTotal
//    Map<String,BigDecimal> dividendsTotal
//
//    List<BSPeriodDateRangeChild> isPeriodDateRangeChildList
//
//    Map<String,BigDecimal> assets
//    Map<String,BigDecimal> liabilities
//    Map<String,BigDecimal> equity
//    Map<String,BigDecimal> totalLiabilitiesAndEquity
//    Map<String,BigDecimal> totalCurrentAssets
//    Map<String,BigDecimal> totalNonCurrentAssets
//    Map<String,BigDecimal> totalCurrentLiabilities
//    Map<String,BigDecimal> totalNonCurrentLiabilities
//    String period
//}


@Transactional(rollbackOn = [Exception.class])
@GraphQLApi
@Component
class BalanceSheetServices {

    @Autowired
    EntityManager entityManager


//
////    Balance Sheet
//    List<IncomeStatementDto> getBalanceSheetPerPeriod (String start,String end){
//        return entityManager.createNativeQuery("""
//            select
//            to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') as "transactionDate",
//            cast(ld.journal_account -> 'motherAccount' ->> 'code' as varchar) as "code",
//            ld.journal_account -> 'motherAccount' ->> 'description' as "account",
//            coa.account_type as "accountType",
//            ld.journal_account -> 'motherAccount' ->> 'normalSide' as "normalSide",
//            case
//                when ld.journal_account -> 'motherAccount' ->> 'normalSide' = 'DEBIT' then (coalesce(sum(ld.debit),0)-coalesce(sum(ld.credit),0))
//            else  (coalesce(sum(ld.credit),0)-coalesce(sum(ld.debit),0))
//            end as "amount"
//            from accounting.ledger_date ld
//            left join accounting.header_ledger hl on hl.id  = ld."header"
//            left join accounting.chart_of_accounts coa on coa.account_code = ld.journal_account -> 'motherAccount' ->> 'code'
//            where
//            coa.account_type in ('ASSET','LIABILITY','EQUITY') and
//            hl.approved_by is not null
//            and to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD') between cast(:start  as varchar)  and cast(:end as varchar)
//            group  by "account","normalSide","code",coa.account_type, to_char(date(ld.transaction_date + interval '8 hour'),'YYYY-MM-DD')
//            order  by "code"
//        """)
//                .setParameter('start',start)
//                .setParameter('end',end)
//                .unwrap(NativeQuery.class)
//                .setResultTransformer(Transformers.aliasToBean(IncomeStatementDto.class))
//                .getResultList();
//    }
//
//    static String getCalculationType(LocalDate start, LocalDate end){
//        LocalDate firstDayOfMonth = start.with(TemporalAdjusters.firstDayOfMonth())
//        LocalDate lastDayOfStartMonth = start.with(TemporalAdjusters.lastDayOfMonth())
//        LocalDate lastDayOfEndMonth = end.with(TemporalAdjusters.lastDayOfMonth())
//
//        if(((start == firstDayOfMonth) && (lastDayOfStartMonth == end)) && (start.getYear() == end.getYear())){
//            return  'monthly'
//        }
//        else if(((start == firstDayOfMonth) && (lastDayOfEndMonth == end)) && (start.getYear() == end.getYear()))
//        {
//            return  'yearly'
//        }
//        else if(start.getYear() != end.getYear())
//        {
//            return  'custom-yearly'
//        }
//        else {
//            return  'daily'
//        }
//    }
//
//    static String getPeriodLabel(String calculationType, LocalDate start, LocalDate end){
//        def firstFormatter = DateTimeFormatter.ofPattern(" MMM YYYY")
//        def secondFormatter = DateTimeFormatter.ofPattern("YYYY")
//        def thirdFormatter = DateTimeFormatter.ofPattern("d MMM")
//        def fourthFormatter = DateTimeFormatter.ofPattern("d MMM YYYY")
//
//        switch (calculationType){
//            case ('monthly'):
//                return "${start.format(firstFormatter)}"
//                break;
//            case ('yearly'):
//                return "${start.format(secondFormatter)}"
//                break;
//            case ('custom-yearly'):
//                return "${start.format(fourthFormatter)}-${end.format(fourthFormatter)}"
//                break;
//            default:
//                return "${start.format(thirdFormatter)}-${end.format(fourthFormatter)}"
//                break;
//        }
//
//    }
//
//    static LocalDate getDateStartEnd(String calculationType, LocalDate start, LocalDate end, List<BSPeriodDateRangeChild> periodList, String returnType){
//        LocalDate monthStart
//        LocalDate monthEnd
//        Period period = Period.between(start,end)
//        Integer diffMonths = Math.abs(period.getMonths())
//        Integer diffDays = Math.abs(period.getDays())
//        Integer diffYears = Math.abs(period.getYears())
//
//        switch (calculationType){
//            case ('monthly') :
//                monthStart = periodList[0].start.minusMonths(1)
//                monthEnd = monthStart.with(TemporalAdjusters.lastDayOfMonth())
//                break;
//            case ('yearly') :
//                monthStart = periodList[0].start.minusYears(1).with(TemporalAdjusters.firstDayOfMonth())
//                monthEnd = periodList[0].end.minusYears(1).with(TemporalAdjusters.lastDayOfMonth())
//                break;
//            default:
//                monthEnd = periodList[0].start.minusDays(1)
//                monthStart = monthEnd.minusDays(diffDays)
//                if(diffMonths > 0){
//                    monthStart =  monthStart.minusMonths(diffMonths)
//                }
//                if(diffYears > 0){
//                    monthStart =  monthStart.minusYears(diffYears)
//                }
//                break;
//        }
//
//        if(returnType.equalsIgnoreCase('start'))
//            return monthStart
//
//        return monthEnd
//
//    }
//
//    static BSPeriodRangeParent getBalanceSheetPeriodRange(LocalDate start, LocalDate end, Integer numPeriod) {
//        BSPeriodRangeParent periodRangeList = new BSPeriodRangeParent()
//        String calculationType = getCalculationType(start,end)
//        Integer i = 0
//
//        List<BSPeriodDateRangeChild> periodDateRangeChildList = new ArrayList<BSPeriodDateRangeChild>()
//
//        BSPeriodDateRangeChild isPeriodDateRangeChild = new BSPeriodDateRangeChild()
//        isPeriodDateRangeChild.key = i++
//        isPeriodDateRangeChild.label = getPeriodLabel(calculationType,start,end)
//        isPeriodDateRangeChild.start = start
//        isPeriodDateRangeChild.end = end
//        periodDateRangeChildList.push(isPeriodDateRangeChild)
//
//        while (numPeriod > i){
//            BSPeriodDateRangeChild isPeriodDateRangeChildWhile = new BSPeriodDateRangeChild()
//            isPeriodDateRangeChildWhile.start = getDateStartEnd(calculationType, start, end, periodDateRangeChildList, 'start')
//            isPeriodDateRangeChildWhile.end = getDateStartEnd(calculationType, start, end, periodDateRangeChildList, 'end')
//            isPeriodDateRangeChildWhile.label = getPeriodLabel(calculationType,isPeriodDateRangeChildWhile.start,isPeriodDateRangeChildWhile.end)
//            isPeriodDateRangeChildWhile.key = i++
//            periodDateRangeChildList.push(isPeriodDateRangeChildWhile)
//        }
//
//        periodRangeList.periodDateRangeChildList = periodDateRangeChildList
//        periodRangeList.start = periodDateRangeChildList[0].start
//        periodRangeList.end = periodDateRangeChildList[i-1].end
//        return  periodRangeList
//    }
//
//    static MapOfAccountsReturn mapOfAccounts(
//            List<ISPeriodDateRangeChild> isPeriodDateRangeChildList,
//            LocalDate transDate,
//            IncomeStatementDto iSDto,
//            Map<String,Map<String,BigDecimal>> mapAccounts,
//            List<IncomeStatementAccount> iSAccount,
//            Map<String,BigDecimal> totalAccount
//    ){
//        MapOfAccountsReturn mapOAR = new MapOfAccountsReturn()
//        mapOAR.mapAccounts = mapAccounts
//        mapOAR.iSAccount = iSAccount
//        mapOAR.totalAccount = totalAccount
//        isPeriodDateRangeChildList.each {
//            pDR ->
//                def compareStart = transDate <=> pDR.start
//                def compareEnd = transDate <=> pDR.end
//
//                if (! mapOAR.mapAccounts[iSDto.code]) {
//                    mapOAR.iSAccount.push(new IncomeStatementAccount(code: iSDto.code, account: iSDto.account))
//                    Map<String,BigDecimal> mapISAccount = [:]
//                    mapISAccount[pDR.key.toString()] = 0.00
//                    mapOAR.mapAccounts[iSDto.code] = mapISAccount as Map<String,BigDecimal>
//                }
//
//                if (compareStart >= 0 && compareEnd <= 0) {
//                    if(!mapOAR.mapAccounts[iSDto.code][pDR.key.toString()]){
//                        mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = 0.00
//                    }
//                    mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] + iSDto.amount
//                    mapOAR.totalAccount[pDR.key.toString()] = mapOAR.totalAccount[pDR.key.toString()] ? mapOAR.totalAccount[pDR.key.toString()] : 0.00
//                    mapOAR.totalAccount[pDR.key.toString()] = mapOAR.totalAccount[pDR.key.toString()] + iSDto.amount
//                }
//
////                                For comparison every 2 consecutive dates
//                if(((pDR.key as Integer)%2) == 0 && pDR.key > 0){
//                    Integer keys = pDR.key
//                    BigDecimal firstVal = mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] : 0.00
//                    BigDecimal secondVal = mapOAR.mapAccounts[iSDto.code][(keys-1).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-1).toString()] : 0.00
//                    mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = firstVal - secondVal
//
//                    BigDecimal firstValTotal = mapOAR.totalAccount[(keys-2).toString()] ? mapOAR.totalAccount[(keys-2).toString()] : 0.00
//                    BigDecimal secondValTotal = mapOAR.totalAccount[(keys-1).toString()] ? mapOAR.totalAccount[(keys-1).toString()] : 0.00
//                    mapOAR.totalAccount[pDR.key.toString()] = firstValTotal - secondValTotal
//                }
//
//                if((((pDR.key as Integer)-1)%2) == 0 && ((pDR.key as Integer)-1) > 0){
//                    Integer keys = pDR.key
//                    BigDecimal firstVal = mapOAR.mapAccounts[iSDto.code][(keys-3).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-3).toString()] : 0.00
//                    BigDecimal secondVal = mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] ? mapOAR.mapAccounts[iSDto.code][(keys-2).toString()] : 0.00
//                    if(secondVal > 0) {
//                        mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = (firstVal - secondVal) / secondVal
//                    }
//                    else {
//                        mapOAR.mapAccounts[iSDto.code][pDR.key.toString()] = 0.00 as BigDecimal
//                    }
//
//                    BigDecimal firstValTotal = mapOAR.totalAccount[(keys-3).toString()] ? mapOAR.totalAccount[(keys-3).toString()] : 0.00
//                    BigDecimal secondValTotal = mapOAR.totalAccount[(keys-2).toString()] ? mapOAR.totalAccount[(keys-2).toString()] : 0.00
//                    if(secondValTotal > 0) {
//                        mapOAR.totalAccount[pDR.key.toString()] = (firstValTotal - secondValTotal) / secondValTotal
//                    }
//                    else {
//                        mapOAR.totalAccount[pDR.key.toString()] = 0.00
//                    }
//                }
//        }
//
//        return mapOAR
//    }
//
//    @GraphQLQuery(name = "getBalanceSheets")
//    BalanceSheetPage getBalanceSheets(
//            @GraphQLArgument(name="start") String start,
//            @GraphQLArgument(name="end") String end,
//            @GraphQLArgument(name="period") Integer period
//    ){
//        def formatter = DateTimeFormatter.ofPattern(" d MMMM YYYY")
//        LocalDate localStart = LocalDate.parse(start)
//        LocalDate localEnd = LocalDate.parse(end)
//
//        BSPeriodRangeParent periodRange = getBalanceSheetPeriodRange(localStart,localEnd,period+1)
//
//        BalanceSheetPage balanceSheetPage = new BalanceSheetPage()
//
//        List<IncomeStatementDto> incomeStatementDtoList = getBalanceSheetPerPeriod(periodRange.start,periodRange.end)
//
//        balanceSheetPage.period = "${localStart.format(formatter)} to ${localEnd.format(formatter)}"
//
//        // ASSETS
//        // CURRENT ASSETS
//        List<BalanceSheetAccount> cashEquiv = new ArrayList<BalanceSheetAccount>()
//        List<String> cashEquivAccounts = Arrays.asList('100010','100020','100030','100040','100050','100060')
//
//        List<BalanceSheetAccount> tradeReceivable = new ArrayList<BalanceSheetAccount>()
//        List<String> tradeReceivableAccounts = Arrays.asList('100090','100110','100100','100130')
//
//        List<BalanceSheetAccount> allowDbtAcc = new ArrayList<BalanceSheetAccount>()
//        List<String> allowDbtAccAccounts = Arrays.asList('100140')
//
//        List<BalanceSheetAccount> advances = new ArrayList<BalanceSheetAccount>()
//        List<String> advancesAccounts = Arrays.asList('100140')
//
//        List<BalanceSheetAccount> inventory = new ArrayList<BalanceSheetAccount>()
//        List<String> inventoryAccounts = Arrays.asList('100150')
//
//        List<BalanceSheetAccount> toolsSupplies = new ArrayList<BalanceSheetAccount>()
//        List<String> toolsSuppliesAccounts = Arrays.asList('100155','100160','100170','100180','100190')
//
//        List<BalanceSheetAccount> inputTax = new ArrayList<BalanceSheetAccount>()
//        List<String> inputTaxAccounts = Arrays.asList('100165')
//
//        List<BalanceSheetAccount> prePayments = new ArrayList<BalanceSheetAccount>()
//        List<String> prePaymentsAccounts = Arrays.asList('100200','100210', '100220', '100230', '100240')
//
//        List<BalanceSheetAccount> otherCurrAss = new ArrayList<BalanceSheetAccount>()
//        List<String> otherCurrAssAccounts = Arrays.asList('100250')
//
//        Map<String,Map<String,BigDecimal>> mapCashEquiv = [:]
//        Map<String,Map<String,BigDecimal>> mapTradeReceivable = [:]
//        Map<String,Map<String,BigDecimal>> mapAllowDbtAcc = [:]
//        Map<String,Map<String,BigDecimal>> mapAdvances = [:]
//        Map<String,Map<String,BigDecimal>> mapInventory = [:]
//        Map<String,Map<String,BigDecimal>> mapToolsSupplies = [:]
//        Map<String,Map<String,BigDecimal>> mapInputTax = [:]
//        Map<String,Map<String,BigDecimal>> mapPrePayments = [:]
//        Map<String,Map<String,BigDecimal>> mapOtherCurrAss = [:]
//
//        Map<String,BigDecimal> totalCashEquiv = [:]
//        Map<String,BigDecimal> totalTradeReceivable = [:]
//        Map<String,BigDecimal> totalAllowDbtAcc = [:]
//        Map<String,BigDecimal> totalAdvances = [:]
//        Map<String,BigDecimal> totalInventory = [:]
//        Map<String,BigDecimal> totalToolsSupplies = [:]
//        Map<String,BigDecimal> totalInputTax = [:]
//        Map<String,BigDecimal> totalPrePayments = [:]
//        Map<String,BigDecimal> totalOtherCurrAss = [:]
//
//        // NON CURRENT ASSETS
//        List<BalanceSheetAccount> loansReceivable = new ArrayList<BalanceSheetAccount>()
//        List<String> loansReceivableAccounts = Arrays.asList('100260')
//
//        List<BalanceSheetAccount> propPlantEquipment = new ArrayList<BalanceSheetAccount>()
//        List<String> propPlantEquipmentAccounts = Arrays.asList('100270')
//
//        List<BalanceSheetAccount> accumulatedDep = new ArrayList<BalanceSheetAccount>()
//        List<String> accumulatedDepAccounts = Arrays.asList('100290')
//
//        List<BalanceSheetAccount> constructionInProg = new ArrayList<BalanceSheetAccount>()
//        List<String> constructionInProgAccounts = Arrays.asList('100300')
//
//        List<BalanceSheetAccount> intangibleAssets = new ArrayList<BalanceSheetAccount>()
//        List<String> intangibleAssetsAccounts = Arrays.asList('100280')
//
//        List<BalanceSheetAccount> defferedTaxAssets = new ArrayList<BalanceSheetAccount>()
//        List<String> defferedTaxAssetsAccounts = Arrays.asList('100320')
//
//        List<BalanceSheetAccount> otherNonCurrentAssets = new ArrayList<BalanceSheetAccount>()
//        List<String> otherNonCurrentAssetsAccounts = Arrays.asList('100310')
//
//        Map<String,Map<String,BigDecimal>> mapLoansReceivable = [:]
//        Map<String,Map<String,BigDecimal>> mapPropPlantEquipment = [:]
//        Map<String,Map<String,BigDecimal>> mapAccumulatedDep = [:]
//        Map<String,Map<String,BigDecimal>> mapConstructionInProg = [:]
//        Map<String,Map<String,BigDecimal>> mapIntangibleAssets = [:]
//        Map<String,Map<String,BigDecimal>> mapDefferedTaxAssets = [:]
//        Map<String,Map<String,BigDecimal>> mapOtherNonCurrentAssets = [:]
//
//        Map<String,BigDecimal> totalLoansReceivable = [:]
//        Map<String,BigDecimal> totalPropPlantEquipment = [:]
//        Map<String,BigDecimal> totalAccumulatedDep = [:]
//        Map<String,BigDecimal> totalConstructionInProg = [:]
//        Map<String,BigDecimal> totalIntangibleAssets = [:]
//        Map<String,BigDecimal> totalDefferedTaxAssets = [:]
//        Map<String,BigDecimal> totalOtherNonCurrentAssets = [:]
//
//        // LIABILITIES
//        // CURRENT LIABILITIES
//        List<BalanceSheetAccount> dueToDoctors = new ArrayList<BalanceSheetAccount>()
//        List<String> dueToDoctorsAccounts = Arrays.asList('200010')
//        List<BalanceSheetAccount> accountsPayable = new ArrayList<BalanceSheetAccount>()
//        List<String> accountsPayableAccounts = Arrays.asList('200020')
//        List<BalanceSheetAccount> nonPayableCurrent = new ArrayList<BalanceSheetAccount>()
//        List<String> nonPayableCurrentAccounts = Arrays.asList('200030')
//        List<BalanceSheetAccount> accruedInterestPayable = new ArrayList<BalanceSheetAccount>()
//        List<String> accruedInterestPayableAccounts = Arrays.asList('200050')
//        List<BalanceSheetAccount> accruedSalariesAndWagesPayable = new ArrayList<BalanceSheetAccount>()
//        List<String> accruedSalariesAndWagesPayableAccounts = Arrays.asList('200070')
//        List<BalanceSheetAccount> accruedVLSPayable = new ArrayList<BalanceSheetAccount>()
//        List<String> accruedVLSPayableAccounts = Arrays.asList('200080')
//        List<BalanceSheetAccount> benefits = new ArrayList<BalanceSheetAccount>()
//        List<String> benefitsAccounts = Arrays.asList('200090','200100','200110','200120','200130')
//        List<BalanceSheetAccount> advancesToEmp = new ArrayList<BalanceSheetAccount>()
//        List<String> advancesToEmpAccounts = Arrays.asList('200140')
//        List<BalanceSheetAccount> taxesPayable = new ArrayList<BalanceSheetAccount>()
//        List<String> taxesPayableAccounts = Arrays.asList('200160')
//        List<BalanceSheetAccount> permitAndLiscensPayables = new ArrayList<BalanceSheetAccount>()
//        List<String> permitAndLiscensPayablesAccounts = Arrays.asList('200170')
//        List<BalanceSheetAccount> otherCurrentPayables = new ArrayList<BalanceSheetAccount>()
//        List<String> otherCurrentPayablesAccounts = Arrays.asList('200180')
//
//        Map<String,Map<String,BigDecimal>> dueToDoctorsMap  = [:]
//        Map<String,Map<String,BigDecimal>> accountsPayableMap  = [:]
//        Map<String,Map<String,BigDecimal>> nonPayableCurrentMap  = [:]
//        Map<String,Map<String,BigDecimal>> accruedInterestPayableMap  = [:]
//        Map<String,Map<String,BigDecimal>> accruedSalariesAndWagesPayableMap  = [:]
//        Map<String,Map<String,BigDecimal>> accruedVLSPayableMap  = [:]
//        Map<String,Map<String,BigDecimal>> benefitsMap  = [:]
//        Map<String,Map<String,BigDecimal>> advancesToEmpMap  = [:]
//        Map<String,Map<String,BigDecimal>> taxesPayableMap  = [:]
//        Map<String,Map<String,BigDecimal>> permitAndLiscensPayablesMap  = [:]
//        Map<String,Map<String,BigDecimal>> otherCurrentPayablesMap  = [:]
//
//        Map<String,BigDecimal> dueToDoctorsTotal  = [:]
//        Map<String,BigDecimal> accountsPayableTotal  = [:]
//        Map<String,BigDecimal> nonPayableCurrentTotal  = [:]
//        Map<String,BigDecimal> accruedInterestPayableTotal  = [:]
//        Map<String,BigDecimal> accruedSalariesAndWagesPayableTotal  = [:]
//        Map<String,BigDecimal> accruedVLSPayableTotal  = [:]
//        Map<String,BigDecimal> benefitsTotal  = [:]
//        Map<String,BigDecimal> advancesToEmpTotal  = [:]
//        Map<String,BigDecimal> taxesPayableTotal  = [:]
//        Map<String,BigDecimal> permitAndLiscensPayablesTotal  = [:]
//        Map<String,BigDecimal> otherCurrentPayablesTotal  = [:]
//        //NON CURRENT
//
//        List<BalanceSheetAccount> advancesToShareHolders = new ArrayList<BalanceSheetAccount>()
//        List<String> advancesToShareHoldersAccounts = Arrays.asList('200195')
//        List<BalanceSheetAccount> notesPayableNonCurrent = new ArrayList<BalanceSheetAccount>()
//        List<String> notesPayableNonCurrentAccounts = Arrays.asList('200200')
//        List<BalanceSheetAccount> otherNonCurrentLiabilities = new ArrayList<BalanceSheetAccount>()
//        List<String> otherNonCurrentLiabilitiesAccounts = Arrays.asList('200210','200220')
//
//        Map<String,Map<String,BigDecimal>> advancesToShareHoldersMap  = [:]
//        Map<String,Map<String,BigDecimal>> notesPayableNonCurrentMap  = [:]
//        Map<String,Map<String,BigDecimal>> otherNonCurrentLiabilitiesMap  = [:]
//
//        Map<String,BigDecimal> advancesToShareHoldersTotal   = [:]
//        Map<String,BigDecimal> notesPayableNonCurrentTotal  = [:]
//        Map<String,BigDecimal> otherNonCurrentLiabilitiesTotal  = [:]
//
//        //  SHARE HOLDERS EQUITY
//        List<BalanceSheetAccount> shareCapital = new ArrayList<BalanceSheetAccount>()
//        List<String> shareCapitalAccounts = Arrays.asList('300010')
//        List<BalanceSheetAccount> additionalPaidCapital = new ArrayList<BalanceSheetAccount>()
//        List<String> additionalPaidCapitalAccounts = Arrays.asList('300020')
//        List<BalanceSheetAccount> retainedEarnings = new ArrayList<BalanceSheetAccount>()
//        List<String> retainedEarningsAccounts = Arrays.asList('300030')
//        List<BalanceSheetAccount> dividends = new ArrayList<BalanceSheetAccount>()
//        List<String> dividendsAccounts = Arrays.asList('300050')
//
//
//        Map<String,Map<String,BigDecimal>> shareCapitalMap = [:]
//        Map<String,Map<String,BigDecimal>> additionalPaidCapitalMap = [:]
//        Map<String,Map<String,BigDecimal>> retainedEarningsMap = [:]
//        Map<String,Map<String,BigDecimal>> dividendsMap = [:]
//
//        Map<String,BigDecimal> shareCapitalTotal = [:]
//        Map<String,BigDecimal> additionalPaidCapitalTotal = [:]
//        Map<String,BigDecimal> retainedEarningsTotal = [:]
//        Map<String,BigDecimal> dividendsTotal = [:]
//
//        List<BSPeriodDateRangeChild> isPeriodDateRangeChildList = new ArrayList<BSPeriodDateRangeChild>()
//        Integer count = 0
//        periodRange.periodDateRangeChildList = periodRange.periodDateRangeChildList.toSorted{ a, b -> a.key <=> b.key }
//        periodRange.periodDateRangeChildList.each {
//            totalCashEquiv[it.key as String] = 0.00
//            totalTradeReceivable[it.key as String] = 0.00
//            totalAllowDbtAcc[it.key as String] = 0.00
//            totalAdvances[it.key as String] = 0.00
//            totalInventory[it.key as String] = 0.00
//            totalToolsSupplies[it.key as String] = 0.00
//
//            BSPeriodDateRangeChild isPeriodDateRangeChild = new BSPeriodDateRangeChild()
//            isPeriodDateRangeChild.key = count
//            isPeriodDateRangeChild.label = it.label
//            isPeriodDateRangeChild.start = it.start
//            isPeriodDateRangeChild.end = it.end
//            isPeriodDateRangeChildList.push(isPeriodDateRangeChild)
//            count++
//
//            if((count+1)%2){
//                BSPeriodDateRangeChild isPeriodDateRangeChild1 = new BSPeriodDateRangeChild()
//                isPeriodDateRangeChild1.key = count++
//                isPeriodDateRangeChild1.label = 'Increase (Decrease)'
//                isPeriodDateRangeChildList.push(isPeriodDateRangeChild1)
//
//                BSPeriodDateRangeChild isPeriodDateRangeChild2 = new BSPeriodDateRangeChild()
//                isPeriodDateRangeChild2.key = count++
//                isPeriodDateRangeChild2.label = '%'
//                isPeriodDateRangeChildList.push(isPeriodDateRangeChild2)
//            }
//
//        }
//
//        isPeriodDateRangeChildList = isPeriodDateRangeChildList.toSorted{ a, b -> a.key <=> b.key }
//
//        if(incomeStatementDtoList) {
//
//            (incomeStatementDtoList as List<IncomeStatementDto>).each {
//                it ->
//                    LocalDate transDate = LocalDate.parse(it.transactionDate)
//
//                    if (it.accountType.equalsIgnoreCase('ASSET')) {
//                        // CURRENT ASSETS
//                        // CASH EQUIVALENT
//                        if (cashEquivAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapCashEquiv, cashEquiv, totalCashEquiv)
//                            cashEquiv = mapOAR.iSAccount
//                            mapCashEquiv = mapOAR.mapAccounts
//                            totalCashEquiv = mapOAR.totalAccount
//                        }
//                        // Trade Receivable
//                        else if (tradeReceivableAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapTradeReceivable, tradeReceivable, totalTradeReceivable)
//                            tradeReceivable = mapOAR.iSAccount
//                            mapTradeReceivable = mapOAR.mapAccounts
//                            totalTradeReceivable = mapOAR.totalAccount
//                        }
//                        // Allowance
//                        else if (allowDbtAccAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapAllowDbtAcc, allowDbtAcc, totalAllowDbtAcc)
//                            allowDbtAcc = mapOAR.iSAccount
//                            mapAllowDbtAcc = mapOAR.mapAccounts
//                            totalAllowDbtAcc = mapOAR.totalAccount
//                        }
//                        // Advances
//                        else if (advancesAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapAdvances, advances, totalAdvances)
//                            advances = mapOAR.iSAccount
//                            mapAdvances = mapOAR.mapAccounts
//                            totalAdvances = mapOAR.totalAccount
//                        }
//                        // Inventory, end (physical count)
//                        else if (inventoryAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapInventory, inventory, totalInventory)
//                            inventory = mapOAR.iSAccount
//                            mapInventory = mapOAR.mapAccounts
//                            totalInventory = mapOAR.totalAccount
//                        }
//                        // Tools and Supplies
//                        else if (toolsSuppliesAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapToolsSupplies, toolsSupplies, totalToolsSupplies)
//                            toolsSupplies = mapOAR.iSAccount
//                            mapToolsSupplies = mapOAR.mapAccounts
//                            totalToolsSupplies = mapOAR.totalAccount
//                        }
//                        // Input Tax
//                        else if (inputTaxAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapInputTax, inputTax, totalInputTax)
//                            inputTax = mapOAR.iSAccount
//                            mapInputTax = mapOAR.mapAccounts
//                            totalInputTax = mapOAR.totalAccount
//                        }
//                        // Pre payments
//                        else if (prePaymentsAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapPrePayments, prePayments, totalPrePayments)
//                            prePayments = mapOAR.iSAccount
//                            mapPrePayments = mapOAR.mapAccounts
//                            totalPrePayments = mapOAR.totalAccount
//                        }
//                        // Other Current Assets
//                        else if (otherCurrAssAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapOtherCurrAss, otherCurrAss, totalOtherCurrAss)
//                            otherCurrAss = mapOAR.iSAccount
//                            mapOtherCurrAss = mapOAR.mapAccounts
//                            totalOtherCurrAss = mapOAR.totalAccount
//                        }
//                        // NON CURRENT ASSETS
//                        // LOANS RECEIVABLES
//                        else if (loansReceivableAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapLoansReceivable, loansReceivable, totalLoansReceivable)
//                            loansReceivable = mapOAR.iSAccount
//                            mapLoansReceivable = mapOAR.mapAccounts
//                            totalLoansReceivable = mapOAR.totalAccount
//                        }
//                        // Property Plan Equipment
//                        else if (propPlantEquipmentAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapPropPlantEquipment, propPlantEquipment, totalPropPlantEquipment)
//                            propPlantEquipment = mapOAR.iSAccount
//                            mapPropPlantEquipment = mapOAR.mapAccounts
//                            totalPropPlantEquipment = mapOAR.totalAccount
//                        } else if (accumulatedDepAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapAccumulatedDep, accumulatedDep, totalAccumulatedDep)
//                            accumulatedDep = mapOAR.iSAccount
//                            mapAccumulatedDep = mapOAR.mapAccounts
//                            totalAccumulatedDep = mapOAR.totalAccount
//                        } else if (constructionInProgAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapConstructionInProg, constructionInProg, totalConstructionInProg)
//                            constructionInProg = mapOAR.iSAccount
//                            mapConstructionInProg = mapOAR.mapAccounts
//                            totalConstructionInProg = mapOAR.totalAccount
//                        } else if (intangibleAssetsAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapIntangibleAssets, intangibleAssets, totalIntangibleAssets)
//                            intangibleAssets = mapOAR.iSAccount
//                            mapIntangibleAssets = mapOAR.mapAccounts
//                            totalIntangibleAssets = mapOAR.totalAccount
//                        } else if (defferedTaxAssetsAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapDefferedTaxAssets, defferedTaxAssets, totalDefferedTaxAssets)
//                            defferedTaxAssets = mapOAR.iSAccount
//                            mapDefferedTaxAssets = mapOAR.mapAccounts
//                            totalDefferedTaxAssets = mapOAR.totalAccount
//                        } else if (otherNonCurrentAssetsAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, mapOtherNonCurrentAssets, otherNonCurrentAssets, totalOtherNonCurrentAssets)
//                            otherNonCurrentAssets = mapOAR.iSAccount
//                            mapOtherNonCurrentAssets = mapOAR.mapAccounts
//                            totalOtherNonCurrentAssets = mapOAR.totalAccount
//                        }
//                    }
//
//                    if (it.accountType.equalsIgnoreCase('LIABILITY')) {
//                        // LIABILITIES
//                        // CURRENT LIABILITIES
//                        if (dueToDoctorsAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, dueToDoctorsMap, dueToDoctors, dueToDoctorsTotal)
//                            dueToDoctors = mapOAR.iSAccount
//                            dueToDoctorsMap = mapOAR.mapAccounts
//                            dueToDoctorsTotal = mapOAR.totalAccount
//                        } else if (accountsPayableAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accountsPayableMap, accountsPayable, accountsPayableTotal)
//                            accountsPayable = mapOAR.iSAccount
//                            accountsPayableMap = mapOAR.mapAccounts
//                            accountsPayableTotal = mapOAR.totalAccount
//                        } else if (nonPayableCurrentAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, nonPayableCurrentMap, nonPayableCurrent, nonPayableCurrentTotal)
//                            nonPayableCurrent = mapOAR.iSAccount
//                            nonPayableCurrentMap = mapOAR.mapAccounts
//                            nonPayableCurrentTotal = mapOAR.totalAccount
//                        } else if (accruedInterestPayableAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accruedInterestPayableMap, accruedInterestPayable, accruedInterestPayableTotal)
//                            accruedInterestPayable = mapOAR.iSAccount
//                            accruedInterestPayableMap = mapOAR.mapAccounts
//                            accruedInterestPayableTotal = mapOAR.totalAccount
//                        } else if (accruedSalariesAndWagesPayableAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accruedSalariesAndWagesPayableMap, accruedSalariesAndWagesPayable, accruedSalariesAndWagesPayableTotal)
//                            accruedSalariesAndWagesPayable = mapOAR.iSAccount
//                            accruedSalariesAndWagesPayableMap = mapOAR.mapAccounts
//                            accruedSalariesAndWagesPayableTotal = mapOAR.totalAccount
//                        } else if (accruedVLSPayableAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, accruedVLSPayableMap, accruedVLSPayable, accruedVLSPayableTotal)
//                            accruedVLSPayable = mapOAR.iSAccount
//                            accruedVLSPayableMap = mapOAR.mapAccounts
//                            accruedVLSPayableTotal = mapOAR.totalAccount
//                        } else if (benefitsAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, benefitsMap, benefits, benefitsTotal)
//                            benefits = mapOAR.iSAccount
//                            benefitsMap = mapOAR.mapAccounts
//                            benefitsTotal = mapOAR.totalAccount
//                        } else if (advancesToEmpAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, advancesToEmpMap, advancesToEmp, advancesToEmpTotal)
//                            advancesToEmp = mapOAR.iSAccount
//                            advancesToEmpMap = mapOAR.mapAccounts
//                            advancesToEmpTotal = mapOAR.totalAccount
//                        } else if (taxesPayableAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, taxesPayableMap, taxesPayable, taxesPayableTotal)
//                            taxesPayable = mapOAR.iSAccount
//                            taxesPayableMap = mapOAR.mapAccounts
//                            taxesPayableTotal = mapOAR.totalAccount
//                        } else if (permitAndLiscensPayablesAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, permitAndLiscensPayablesMap, permitAndLiscensPayables, permitAndLiscensPayablesTotal)
//                            permitAndLiscensPayables = mapOAR.iSAccount
//                            permitAndLiscensPayablesMap = mapOAR.mapAccounts
//                            permitAndLiscensPayablesTotal = mapOAR.totalAccount
//                        } else if (otherCurrentPayablesAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, otherCurrentPayablesMap, otherCurrentPayables, otherCurrentPayablesTotal)
//                            otherCurrentPayables = mapOAR.iSAccount
//                            otherCurrentPayablesMap = mapOAR.mapAccounts
//                            otherCurrentPayablesTotal = mapOAR.totalAccount
//                        }
//                        // NON CURRENT
//                        else if (advancesToShareHoldersAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, advancesToShareHoldersMap, advancesToShareHolders, advancesToShareHoldersTotal)
//                            advancesToShareHolders = mapOAR.iSAccount
//                            advancesToShareHoldersMap = mapOAR.mapAccounts
//                            advancesToShareHoldersTotal = mapOAR.totalAccount
//                        } else if (notesPayableNonCurrentAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, notesPayableNonCurrentMap, notesPayableNonCurrent, notesPayableNonCurrentTotal)
//                            notesPayableNonCurrent = mapOAR.iSAccount
//                            notesPayableNonCurrentMap = mapOAR.mapAccounts
//                            notesPayableNonCurrentTotal = mapOAR.totalAccount
//                        } else if (otherNonCurrentLiabilitiesAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, otherNonCurrentLiabilitiesMap, otherNonCurrentLiabilities, otherNonCurrentLiabilitiesTotal)
//                            otherNonCurrentLiabilities = mapOAR.iSAccount
//                            otherNonCurrentLiabilitiesMap = mapOAR.mapAccounts
//                            otherNonCurrentLiabilitiesTotal = mapOAR.totalAccount
//                        }
//                    }
//
//                    if (it.accountType.equalsIgnoreCase('EQUITY')) {
//                        if (shareCapitalAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, shareCapitalMap, shareCapital, shareCapitalTotal)
//                            shareCapital = mapOAR.iSAccount
//                            shareCapitalMap = mapOAR.mapAccounts
//                            shareCapitalTotal = mapOAR.totalAccount
//                        } else if (additionalPaidCapitalAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, additionalPaidCapitalMap, additionalPaidCapital, additionalPaidCapitalTotal)
//                            additionalPaidCapital = mapOAR.iSAccount
//                            additionalPaidCapitalMap = mapOAR.mapAccounts
//                            additionalPaidCapitalTotal = mapOAR.totalAccount
//                        } else if (retainedEarningsAccounts.contains(it.code)) {
//                            MapOfAccountsReturn mapOAR =
//                                    mapOfAccounts(isPeriodDateRangeChildList, transDate, it, retainedEarningsMap, retainedEarnings, retainedEarningsTotal)
//                            retainedEarnings = mapOAR.iSAccount
//                            retainedEarningsMap = mapOAR.mapAccounts
//                            retainedEarningsTotal = mapOAR.totalAccount
//                        }
//                    }
//            }
//
//        }
//
//
//
//
//        Map<String,BigDecimal> assets = [:]
//        Map<String,BigDecimal> liabilities = [:]
//        Map<String,BigDecimal> equity = [:]
//        Map<String,BigDecimal> totalLiabilitiesAndEquity = [:]
//        Map<String,BigDecimal> totalCurrentAssets = [:]
//        Map<String,BigDecimal> totalNonCurrentAssets = [:]
//        Map<String,BigDecimal> totalCurrentLiabilities = [:]
//        Map<String,BigDecimal> totalNonCurrentLiabilities = [:]
//        Map<String,BigDecimal> totalEquity = [:]
//
//        isPeriodDateRangeChildList.each {
//            it ->
//                // Current Assets
//                BigDecimal totalCE = totalCashEquiv[it.key.toString()] ? totalCashEquiv[it.key.toString()] : 0.00
//                BigDecimal totalTR = totalTradeReceivable[it.key.toString()] ? totalTradeReceivable[it.key.toString()] : 0.00
//                BigDecimal totalADA = totalAllowDbtAcc[it.key.toString()] ? totalAllowDbtAcc[it.key.toString()] : 0.00
//                BigDecimal totalAdv = totalAdvances[it.key.toString()] ? totalAdvances[it.key.toString()] : 0.00
//                BigDecimal totalInv = totalInventory[it.key.toString()] ? totalInventory[it.key.toString()] : 0.00
//                BigDecimal totalTS = totalToolsSupplies[it.key.toString()] ? totalToolsSupplies[it.key.toString()] : 0.00
//                BigDecimal totalIT = totalInputTax[it.key.toString()] ? totalInputTax[it.key.toString()] : 0.00
//                BigDecimal totalPP = totalPrePayments[it.key.toString()] ? totalPrePayments[it.key.toString()] : 0.00
//                BigDecimal totalOCA = totalOtherCurrAss[it.key.toString()] ? totalOtherCurrAss[it.key.toString()] : 0.00
//                totalCurrentAssets[it.key.toString()] =
//                        totalCE + totalTR + totalAdv + totalInv + totalTS + totalIT + totalPP + totalOCA - totalADA
//
//                BigDecimal totalNonLR = totalLoansReceivable[it.key.toString()] ? totalLoansReceivable[it.key.toString()] : 0.00
//                BigDecimal totalNonPPE = totalPropPlantEquipment[it.key.toString()] ? totalPropPlantEquipment[it.key.toString()] : 0.00
//                BigDecimal totalNonACD = totalAccumulatedDep[it.key.toString()] ? totalAccumulatedDep[it.key.toString()] : 0.00
//                BigDecimal totalNonCIP = totalConstructionInProg[it.key.toString()] ? totalConstructionInProg[it.key.toString()] : 0.00
//                BigDecimal totalNonITA = totalIntangibleAssets[it.key.toString()] ? totalIntangibleAssets[it.key.toString()] : 0.00
//                BigDecimal totalNonDTA = totalDefferedTaxAssets[it.key.toString()] ? totalDefferedTaxAssets[it.key.toString()] : 0.00
//                BigDecimal totalNonONC = totalOtherNonCurrentAssets[it.key.toString()] ? totalOtherNonCurrentAssets[it.key.toString()] : 0.00
//                totalNonCurrentAssets[it.key.toString()] =
//                        totalNonLR + totalNonPPE + totalNonCIP + totalNonITA + totalNonDTA + totalNonONC - totalNonACD
//
//                assets[it.key.toString()] = totalCurrentAssets[it.key.toString()] + totalNonCurrentAssets[it.key.toString()]
//
//                //LIABILITIES
//                //CURRENT ASSETS
//                BigDecimal totalLIADTD =  dueToDoctorsTotal[it.key.toString()] ? dueToDoctorsTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAAP =  accountsPayableTotal[it.key.toString()] ? accountsPayableTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIANPC =  nonPayableCurrentTotal[it.key.toString()] ? nonPayableCurrentTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAAIP =  accruedInterestPayableTotal[it.key.toString()] ? accruedInterestPayableTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAASWP =  accruedSalariesAndWagesPayableTotal[it.key.toString()] ? accruedSalariesAndWagesPayableTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAVSP =  accruedVLSPayableTotal[it.key.toString()] ? accruedVLSPayableTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIABT =  benefitsTotal[it.key.toString()] ? benefitsTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAATET =  advancesToEmpTotal[it.key.toString()] ? advancesToEmpTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIATPT =  taxesPayableTotal[it.key.toString()] ? taxesPayableTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAPAL =  permitAndLiscensPayablesTotal[it.key.toString()] ? permitAndLiscensPayablesTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAOCP =  otherCurrentPayablesTotal[it.key.toString()] ? otherCurrentPayablesTotal[it.key.toString()] : 0.00
//                totalCurrentLiabilities[it.key.toString()] =
//                        totalLIADTD + totalLIAAP + totalLIANPC + totalLIAAIP + totalLIAASWP + totalLIABT + totalLIAATET + totalLIATPT + totalLIAPAL + totalLIAOCP + totalLIAVSP
//                // NON CURRENT
//                BigDecimal totalLIAATS =  advancesToShareHoldersTotal[it.key.toString()] ? advancesToShareHoldersTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIANPNC =  notesPayableNonCurrentTotal[it.key.toString()] ? notesPayableNonCurrentTotal[it.key.toString()] : 0.00
//                BigDecimal totalLIAONCLT =  otherNonCurrentLiabilitiesTotal[it.key.toString()] ? otherNonCurrentLiabilitiesTotal[it.key.toString()] : 0.00
//                totalNonCurrentLiabilities[it.key.toString()] = totalLIAATS + totalLIANPNC + totalLIAONCLT
//
//                liabilities[it.key.toString()] = totalCurrentLiabilities[it.key.toString()] + totalNonCurrentLiabilities[it.key.toString()]
//
//                //    EQUITY
//                BigDecimal totalSCT = shareCapitalTotal[it.key.toString()] ? shareCapitalTotal[it.key.toString()] : 0.00
//                BigDecimal totalAPCT = additionalPaidCapitalTotal[it.key.toString()] ? additionalPaidCapitalTotal[it.key.toString()] : 0.00
//                BigDecimal totalRET = retainedEarningsTotal[it.key.toString()] ? retainedEarningsTotal[it.key.toString()] : 0.00
//                BigDecimal totalDEV = dividendsTotal[it.key.toString()] ? dividendsTotal[it.key.toString()] : 0.00
//
//                equity[it.key.toString()] = totalSCT + totalAPCT + totalRET - totalDEV
//
//                totalLiabilitiesAndEquity[it.key.toString()] = liabilities[it.key.toString()] + equity[it.key.toString()]
//        }
//
//
//
//
//        balanceSheetPage.assets = assets
//        balanceSheetPage.liabilities = liabilities
//        balanceSheetPage.equity = equity
//        balanceSheetPage.totalLiabilitiesAndEquity = totalLiabilitiesAndEquity
//
//        balanceSheetPage.totalCurrentAssets = totalCurrentAssets
//        balanceSheetPage.totalNonCurrentAssets = totalNonCurrentAssets
//        balanceSheetPage.totalCurrentLiabilities = totalCurrentLiabilities
//        balanceSheetPage.totalNonCurrentLiabilities = totalNonCurrentLiabilities
//
//
//        balanceSheetPage.isPeriodDateRangeChildList = isPeriodDateRangeChildList
//        /* Assets */
//        // CURRENT ASSETS
//        balanceSheetPage.cashEquiv = cashEquiv
//        balanceSheetPage.mapCashEquiv = mapCashEquiv
//        balanceSheetPage.totalCashEquiv = totalCashEquiv
//
//        balanceSheetPage.tradeReceivable = tradeReceivable
//        balanceSheetPage.mapTradeReceivable = mapTradeReceivable
//        balanceSheetPage.totalTradeReceivable = totalTradeReceivable
//
//        balanceSheetPage.allowDbtAcc = allowDbtAcc
//        balanceSheetPage.mapAllowDbtAcc = mapAllowDbtAcc
//        balanceSheetPage.totalAllowDbtAcc = totalAllowDbtAcc
//
//        balanceSheetPage.advances = advances
//        balanceSheetPage.mapAdvances = mapAdvances
//        balanceSheetPage.totalAdvances = totalAdvances
//
//        balanceSheetPage.inventory = inventory
//        balanceSheetPage.mapInventory = mapInventory
//        balanceSheetPage.totalInventory = totalInventory
//
//        balanceSheetPage.toolsSupplies = toolsSupplies
//        balanceSheetPage.mapToolsSupplies = mapToolsSupplies
//        balanceSheetPage.totalToolsSupplies = totalToolsSupplies
//
//        balanceSheetPage.inputTax = inputTax
//        balanceSheetPage.mapInputTax = mapInputTax
//        balanceSheetPage.totalInputTax = totalInputTax
//
//        balanceSheetPage.prePayments = prePayments
//        balanceSheetPage.mapPrePayments = mapPrePayments
//        balanceSheetPage.totalPrePayments = totalPrePayments
//
//        balanceSheetPage.otherCurrAss = otherCurrAss
//        balanceSheetPage.mapOtherCurrAss = mapOtherCurrAss
//        balanceSheetPage.totalOtherCurrAss = totalOtherCurrAss
//
//        // NON CURRENT ASSETS
//        balanceSheetPage.loansReceivable = loansReceivable
//        balanceSheetPage.propPlantEquipment = propPlantEquipment
//        balanceSheetPage.accumulatedDep = accumulatedDep
//        balanceSheetPage.constructionInProg = constructionInProg
//        balanceSheetPage.intangibleAssets = intangibleAssets
//        balanceSheetPage.defferedTaxAssets = defferedTaxAssets
//        balanceSheetPage.otherNonCurrentAssets = otherNonCurrentAssets
//
//        balanceSheetPage.mapLoansReceivable = mapLoansReceivable
//        balanceSheetPage.mapPropPlantEquipment = mapPropPlantEquipment
//        balanceSheetPage.mapAccumulatedDep = mapAccumulatedDep
//        balanceSheetPage.mapConstructionInProg = mapConstructionInProg
//        balanceSheetPage.mapIntangibleAssets = mapIntangibleAssets
//        balanceSheetPage.mapDefferedTaxAssets = mapDefferedTaxAssets
//        balanceSheetPage.mapOtherNonCurrentAssets = mapOtherNonCurrentAssets
//
//        balanceSheetPage.totalLoansReceivable = totalLoansReceivable
//        balanceSheetPage.totalPropPlantEquipment = totalPropPlantEquipment
//        balanceSheetPage.totalAccumulatedDep = totalAccumulatedDep
//        balanceSheetPage.totalConstructionInProg = totalConstructionInProg
//        balanceSheetPage.totalIntangibleAssets = totalIntangibleAssets
//        balanceSheetPage.totalDefferedTaxAssets = totalDefferedTaxAssets
//        balanceSheetPage.totalOtherNonCurrentAssets = totalOtherNonCurrentAssets
//
//        //LIABILITIES
//        //CURRENT ASSETS
//        balanceSheetPage.dueToDoctors = dueToDoctors
//        balanceSheetPage.accountsPayable = accountsPayable
//        balanceSheetPage.nonPayableCurrent = nonPayableCurrent
//        balanceSheetPage.accruedInterestPayable = accruedInterestPayable
//        balanceSheetPage.accruedSalariesAndWagesPayable = accruedSalariesAndWagesPayable
//        balanceSheetPage.accruedVLSPayable = accruedVLSPayable
//        balanceSheetPage.benefits = benefits
//        balanceSheetPage.advancesToEmp = advancesToEmp
//        balanceSheetPage.taxesPayable = taxesPayable
//        balanceSheetPage.permitAndLiscensPayables = permitAndLiscensPayables
//        balanceSheetPage.otherCurrentPayables = otherCurrentPayables
//
//        balanceSheetPage.dueToDoctorsMap = dueToDoctorsMap
//        balanceSheetPage.accountsPayableMap = accountsPayableMap
//        balanceSheetPage.nonPayableCurrentMap = nonPayableCurrentMap
//        balanceSheetPage.accruedInterestPayableMap = accruedInterestPayableMap
//        balanceSheetPage.accruedSalariesAndWagesPayableMap = accruedSalariesAndWagesPayableMap
//        balanceSheetPage.accruedVLSPayableMap = accruedVLSPayableMap
//        balanceSheetPage.benefitsMap = benefitsMap
//        balanceSheetPage.advancesToEmpMap = advancesToEmpMap
//        balanceSheetPage.taxesPayableMap = taxesPayableMap
//        balanceSheetPage.permitAndLiscensPayablesMap = permitAndLiscensPayablesMap
//        balanceSheetPage.otherCurrentPayablesMap = otherCurrentPayablesMap
//
//        balanceSheetPage.dueToDoctorsTotal = dueToDoctorsTotal
//        balanceSheetPage.accountsPayableTotal = accountsPayableTotal
//        balanceSheetPage.nonPayableCurrentTotal = nonPayableCurrentTotal
//        balanceSheetPage.accruedInterestPayableTotal = accruedInterestPayableTotal
//        balanceSheetPage.accruedSalariesAndWagesPayableTotal = accruedSalariesAndWagesPayableTotal
//        balanceSheetPage.accruedVLSPayableTotal = accruedVLSPayableTotal
//        balanceSheetPage.benefitsTotal = benefitsTotal
//        balanceSheetPage.advancesToEmpTotal = advancesToEmpTotal
//        balanceSheetPage.taxesPayableTotal = taxesPayableTotal
//        balanceSheetPage.permitAndLiscensPayablesTotal = permitAndLiscensPayablesTotal
//        balanceSheetPage.otherCurrentPayablesTotal = otherCurrentPayablesTotal
//        // NON CURRENT LIABILITIES
//        balanceSheetPage.advancesToShareHolders = advancesToShareHolders
//        balanceSheetPage.notesPayableNonCurrent = notesPayableNonCurrent
//        balanceSheetPage.otherNonCurrentLiabilities = otherNonCurrentLiabilities
//
//        balanceSheetPage.advancesToShareHoldersMap = advancesToShareHoldersMap
//        balanceSheetPage.notesPayableNonCurrentMap = notesPayableNonCurrentMap
//        balanceSheetPage.otherNonCurrentLiabilitiesMap = otherNonCurrentLiabilitiesMap
//
//        balanceSheetPage.advancesToShareHoldersTotal = advancesToShareHoldersTotal
//        balanceSheetPage.notesPayableNonCurrentTotal = notesPayableNonCurrentTotal
//        balanceSheetPage.otherNonCurrentLiabilitiesTotal = otherNonCurrentLiabilitiesTotal
//
//        balanceSheetPage.shareCapital = shareCapital
//        balanceSheetPage.additionalPaidCapital = additionalPaidCapital
//        balanceSheetPage.retainedEarnings = retainedEarnings
//        balanceSheetPage.dividends = dividends
//
//        balanceSheetPage.shareCapitalMap = shareCapitalMap
//        balanceSheetPage.additionalPaidCapitalMap = additionalPaidCapitalMap
//        balanceSheetPage.retainedEarningsMap = retainedEarningsMap
//        balanceSheetPage.dividendsMap = dividendsMap
//
//        balanceSheetPage.shareCapitalTotal = shareCapitalTotal
//        balanceSheetPage.additionalPaidCapitalTotal = additionalPaidCapitalTotal
//        balanceSheetPage.retainedEarningsTotal = retainedEarningsTotal
//        balanceSheetPage.dividendsTotal = dividendsTotal
//
//        return balanceSheetPage
//    }
}
