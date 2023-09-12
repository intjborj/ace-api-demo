package com.hisd3.hismk2.graphqlservices.accounting

import com.hisd3.hismk2.domain.accounting.AccountBalances
import com.hisd3.hismk2.domain.accounting.HeaderLedger
import com.hisd3.hismk2.domain.accounting.JournalType
import com.hisd3.hismk2.domain.accounting.LedgerDocType
import com.hisd3.hismk2.graphqlservices.base.AbstractDaoService
import com.hisd3.hismk2.graphqlservices.types.GraphQLResVal
import com.hisd3.hismk2.graphqlservices.types.GraphQLRetVal
import com.hisd3.hismk2.security.SecurityUtils
import com.hisd3.hismk2.services.requestscope.ChartofAccountGenerator
import io.leangen.graphql.annotations.GraphQLArgument
import io.leangen.graphql.annotations.GraphQLMutation
import io.leangen.graphql.annotations.GraphQLQuery
import io.leangen.graphql.spqr.spring.annotations.GraphQLApi
import org.apache.commons.lang3.StringUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

import javax.persistence.EntityManager
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@GraphQLApi
@Service
class AccountBalancesServices extends AbstractDaoService<AccountBalances>{

    AccountBalancesServices(){
        super(AccountBalances.class)
    }


    @Autowired
    ChartofAccountGenerator chartofAccountGenerator

    @Autowired
    LedgerServices ledgerServices

    @Autowired
    EntityManager entityManager

    @Autowired
    FiscalServices fiscalServices

    @GraphQLQuery(name="accountBalancesList")
    List<AccountBalances> accountBalancesList(){
        findAll().sort({a,b->b.startDate <=> a.startDate})
    }

    @GraphQLQuery(name="accountBalancesById")
    AccountBalances accountBalancesById(
            @GraphQLArgument(name="id") UUID id
    ){
        findOne(id)
    }

    @GraphQLMutation(name="upsertAccountBalances")
    AccountBalances upsertAccountBalances(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="fields") Map<String,Object> fields
    ){
        upsertFromMap(id,fields)
    }



    @Transactional
    HeaderLedger createAccountBalancesJE(Date start, Date end){
        def coa =  chartofAccountGenerator.getAccountBalanceChartOfAccount([])
        // validate if code is on cOa

        List<EntryFull> entriesTarget = []
//        coa.each {
//            it->
//                entriesTarget << new EntryFull(it,0.00,0.00)
//        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM")
        LocalDate startLocal = start.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        LocalDate endLocal = end.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        Integer year = startLocal.getYear()
        String monthStart = formatter.format(startLocal)
        String monthEnd = formatter.format(endLocal)
        String invoiceSoaReference = "${year}-BEGBAL"
        String entityName = "${monthStart} - ${monthEnd} ${year}"
        String particulars = "Beginning Balance as of ${monthEnd}"
        String docType = "BB"
        Boolean beginningBalance = true

        try {

            def headerLedger  =   ledgerServices.createDraftHeaderLedgerFull(entriesTarget)
            def login =  SecurityUtils.currentLogin()

            headerLedger.entityName = entityName
            headerLedger.approvedBy = login
            headerLedger.approvedDatetime = Instant.now()
            ledgerServices.validateEntries(headerLedger)

            return ledgerServices.persistHeaderLedger(headerLedger,
                    StringUtils.upperCase(invoiceSoaReference),
                    StringUtils.upperCase(entityName),
                    StringUtils.upperCase(particulars),
                    LedgerDocType.valueOf(docType),
                    JournalType.GENERAL,
                    endLocal.atStartOfDay(ZoneId.systemDefault()).toInstant(),
                    [:],
                    beginningBalance,
                    true
            )
        }catch(ignore){
            return  null
        }

    }

    @Transactional
    @GraphQLMutation(name="createNewAccountBalances")
    GraphQLResVal<AccountBalances> createNewAccountBalances(
            @GraphQLArgument(name="id") UUID id,
            @GraphQLArgument(name="fields") Map<String,Object> fields
    ){
        try{
            if(fields){
                AccountBalances accountBalances  = upsertFromMap(id,fields){
                    it,isInsert ->
                        if(isInsert){
                            HeaderLedger headerLedger = createAccountBalancesJE(it.startDate, it.endDate)
                            if (headerLedger) {
                                it.headerLedgerId = headerLedger.id
                                save(it)
                            }
                        }
                        else {
                            HeaderLedger headerLedger = ledgerServices.findOne(it.headerLedgerId)
                            if(headerLedger){
                                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM")
                                LocalDate startLocal = it.startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                LocalDate endLocal = it.endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                                Integer year = startLocal.getYear()
                                String monthStart = formatter.format(startLocal)
                                String monthEnd = formatter.format(endLocal)
                                headerLedger.transactionDate = it.endDate.toInstant()
                                headerLedger.invoiceSoaReference = "${year}-BEGBAL"
                                headerLedger.entityName = "${monthStart} - ${monthEnd} ${year}"
                                headerLedger.particulars = "Beginning Balance as of ${monthEnd}"
                                headerLedger.fiscal = fiscalServices.findFiscalForTransactionDate(endLocal.atStartOfDay(ZoneId.systemDefault()).toInstant())
                                ledgerServices.save(headerLedger)
                            }
                        }
                }
                return new GraphQLResVal<AccountBalances>(accountBalances,true,"Success")
            }
            return new GraphQLResVal<AccountBalances>(null,false,"No parameter found")
        }catch(e) {
            return new GraphQLResVal<AccountBalances>(null, false, e.message)
        }
    }



}